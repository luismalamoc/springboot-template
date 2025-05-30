package com.example.springboottemplate.client;

import com.example.springboottemplate.dto.ExampleRequest;
import com.example.springboottemplate.dto.ExampleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Example API client that demonstrates how to use WebClient to make API calls
 */
@Component
public class ExampleApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ExampleApiClient.class);

    private final WebClient webClient;
    private final String apiBaseUrl;

    @Autowired
    public ExampleApiClient(WebClient webClient, 
                           @Value("${api.example.base-url:https://api.example.com}") String apiBaseUrl) {
        this.webClient = webClient;
        this.apiBaseUrl = apiBaseUrl;
        logger.info("Initialized ExampleApiClient with base URL: {}", apiBaseUrl);
    }

    /**
     * Example method to fetch data from an API
     * 
     * @param id The resource ID to fetch
     * @return Mono containing the response data
     */
    public Mono<ExampleResponse> getResource(String id) {
        logger.debug("Fetching resource with ID: {}", id);
        
        return webClient.get()
                .uri(apiBaseUrl + "/resources/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ExampleResponse.class)
                .doOnSuccess(response -> logger.debug("Successfully fetched resource: {}", response))
                .doOnError(error -> logger.error("Error fetching resource: {}", error.getMessage()));
    }

    /**
     * Example method to post data to an API
     * 
     * @param request The request data to send
     * @return Mono containing the response data
     */
    public Mono<ExampleResponse> createResource(ExampleRequest request) {
        logger.debug("Creating resource with data: {}", request);
        
        return webClient.post()
                .uri(apiBaseUrl + "/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ExampleResponse.class)
                .doOnSuccess(response -> logger.debug("Successfully created resource: {}", response))
                .doOnError(error -> logger.error("Error creating resource: {}", error.getMessage()));
    }


}
