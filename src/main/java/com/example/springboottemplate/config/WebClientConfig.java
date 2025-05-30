package com.example.springboottemplate.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import com.example.springboottemplate.exception.ServerErrorException;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
public class WebClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${webclient.timeout.connect:60000}")
    private int connectTimeoutMillis;

    @Value("${webclient.timeout.response:120}")
    private int responseTimeoutSeconds;

    @Value("${webclient.timeout.read:120}")
    private int readTimeoutSeconds;

    @Value("${webclient.timeout.write:60}")
    private int writeTimeoutSeconds;

    @Value("${webclient.retry.max:3}")
    private int maxRetries;

    @Value("${webclient.maxInMemorySize:2}")
    private int maxInMemorySize;

    @Value("${webclient.retry.initialBackoff:2}")
    private int initialBackoffSeconds;

    @Value("${webclient.retry.maxBackoff:30}")
    private int maxBackoffSeconds;

    private ExchangeFilterFunction retryFilter(int maxRetries) {
        return (request, next) ->
                next.exchange(request)
                        .flatMap(clientResponse -> {
                            int statusCode = clientResponse.statusCode().value();
                            if (statusCode >= 500) {
                                return Mono.error(new ServerErrorException(statusCode,
                                        "Server error: " + statusCode));
                            }
                            return Mono.just(clientResponse);
                        })
                        .doOnError(throwable -> {
                            // Improved logging for specific connection errors
                            if (isConnectionReset(throwable)) {
                                logger.warn("Connection reset detected, will retry: {} | Error type: {}", 
                                        throwable.getMessage(), throwable.getClass().getSimpleName());
                            } else if (isSocketTimeout(throwable)) {
                                logger.warn("Socket timeout detected, will retry: {}", throwable.getMessage());
                            } else if (throwable instanceof WebClientRequestException) {
                                logger.warn("WebClient request error: {} | URI: {}", 
                                        throwable.getMessage(), 
                                        ((WebClientRequestException) throwable).getUri());
                            }
                        })
                        .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(initialBackoffSeconds))
                                .maxBackoff(Duration.ofSeconds(maxBackoffSeconds))
                                .jitter(0.1) // Add jitter to avoid thundering herd
                                .filter(this::shouldRetry)
                                .doBeforeRetry(retrySignal -> {
                                    // Calculate backoff delay based on attempt number
                                    long attemptNumber = retrySignal.totalRetries() + 1;
                                    long delayMs = Duration.ofSeconds(initialBackoffSeconds)
                                            .multipliedBy((long) Math.pow(2, attemptNumber - 1))
                                            .toMillis();
                                    // Apply max backoff limit
                                    delayMs = Math.min(delayMs, Duration.ofSeconds(maxBackoffSeconds).toMillis());
                                    
                                    logger.warn("Retrying request after {}ms delay. Attempt: {}/{} due to: {} [{}]",
                                            delayMs,
                                            attemptNumber,
                                            maxRetries,
                                            getErrorSummary(retrySignal.failure()),
                                            retrySignal.failure().getClass().getSimpleName());
                                })
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                    logger.error("All {} retry attempts exhausted. Final error: {}", 
                                            maxRetries, getErrorSummary(retrySignal.failure()));
                                    return retrySignal.failure();
                                }));
    }

    /**
     * Determines if retry should be attempted based on error type
     */
    private boolean shouldRetry(Throwable throwable) {
        // Server errors
        if (throwable instanceof ServerErrorException) {
            logger.debug("Server error detected, will retry");
            return true;
        }
        
        // Connection reset errors
        if (isConnectionReset(throwable)) {
            logger.debug("Connection reset detected, will retry");
            return true;
        }
        
        // Socket timeouts
        if (isSocketTimeout(throwable)) {
            logger.debug("Socket timeout detected, will retry");
            return true;
        }
        
        // Rate limiting (429)
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException wcre = (WebClientResponseException) throwable;
            if (wcre.getStatusCode().value() == 429) {
                logger.debug("Rate limit detected, will retry");
                return true;
            }
        }
        
        // General timeouts
        if (throwable instanceof TimeoutException) {
            logger.debug("Timeout detected, will retry");
            return true;
        }
        
        // WebClient request exceptions with network issues
        if (throwable instanceof WebClientRequestException) {
            String message = throwable.getMessage();
            if (message != null && (
                message.contains("Connection refused") ||
                message.contains("No route to host") ||
                message.contains("Name or service not known") ||
                message.contains("timeout")
            )) {
                logger.debug("Network error detected, will retry: {}", message);
                return true;
            }
        }
        
        logger.debug("Error not eligible for retry: {} - {}", 
                throwable.getClass().getSimpleName(), throwable.getMessage());
        return false;
    }

    /**
     * Checks if the error is a "Connection reset" - Enhanced to detect specific Netty error
     */
    private boolean isConnectionReset(Throwable throwable) {
        // Direct message check
        String message = throwable.getMessage();
        if (message != null && message.contains("Connection reset by peer")) {
            return true;
        }
        
        // SocketException check
        if (throwable instanceof java.net.SocketException &&
            message != null && message.contains("Connection reset")) {
            return true;
        }
        
        // WebClientRequestException wrapping connection reset
        if (throwable instanceof WebClientRequestException) {
            WebClientRequestException wcre = (WebClientRequestException) throwable;
            
            // Check the direct message
            if (wcre.getMessage() != null && wcre.getMessage().contains("Connection reset by peer")) {
                return true;
            }
            
            // Check all nested causes (important for Netty error)
            Throwable cause = wcre.getCause();
            while (cause != null) {
                String causeMessage = cause.getMessage();
                if (causeMessage != null && (
                    causeMessage.contains("Connection reset by peer") ||
                    causeMessage.contains("Connection reset") ||
                    causeMessage.contains("recvAddress(..) failed") // Specific to Netty error
                )) {
                    return true;
                }
                
                // Check for Netty native exception
                if (cause.getClass().getName().contains("Errors$NativeIoException") &&
                    causeMessage != null && causeMessage.contains("Connection reset")) {
                    return true;
                }
                
                cause = cause.getCause();
            }
        }
        
        return false;
    }

    /**
     * Checks if the error is a socket timeout
     */
    private boolean isSocketTimeout(Throwable throwable) {
        return (throwable instanceof java.net.SocketTimeoutException) ||
                (throwable instanceof WebClientRequestException &&
                        throwable.getCause() instanceof java.net.SocketTimeoutException) ||
                (throwable instanceof TimeoutException);
    }
    
    /**
     * Gets an error summary for logging
     */
    private String getErrorSummary(Throwable throwable) {
        if (throwable instanceof WebClientRequestException) {
            WebClientRequestException wcre = (WebClientRequestException) throwable;
            return String.format("NetworkError: %s", wcre.getMessage());
        }
        
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException wcre = (WebClientResponseException) throwable;
            return String.format("HttpError[%d]: %s", wcre.getStatusCode().value(), wcre.getMessage());
        }
        
        if (throwable instanceof ServerErrorException) {
            ServerErrorException see = (ServerErrorException) throwable;
            return String.format("ServerError[%d]: %s", see.getStatusCode(), see.getMessage());
        }
        
        return throwable.getMessage() != null ? throwable.getMessage() : throwable.getClass().getSimpleName();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        logger.info("Configuring WebClient with timeouts - Connect: {}ms, Response: {}s, Read: {}s, Write: {}s, MaxRetries: {}", 
                connectTimeoutMillis, responseTimeoutSeconds, readTimeoutSeconds, writeTimeoutSeconds, maxRetries);
        
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                // Add keep-alive and TCP_NODELAY for better performance
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutSeconds, TimeUnit.SECONDS));
                    conn.addHandlerLast(new WriteTimeoutHandler(writeTimeoutSeconds, TimeUnit.SECONDS));
                })
                // Connection logging for debugging
                .doOnConnected(conn -> logger.debug("WebClient connection established"))
                .doOnDisconnected(conn -> logger.debug("WebClient connection closed"));

        // Increase max size to handle large responses
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize * 1024 * 1024))
                .build();

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filter(retryFilter(maxRetries))
                // Add optional logging filter
                .filter(loggingFilter())
                .build();
    }
    
    /**
     * Optional logging filter for requests/responses
     */
    private ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }
}
