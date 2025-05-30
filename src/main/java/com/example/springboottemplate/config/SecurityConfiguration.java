package com.example.springboottemplate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final Long MAX_AGE = 3600L;
    
    @Value("#{'${cors.allowed.origins:*}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(allowedOrigins);
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
                    configuration.setAllowCredentials(true);
                    configuration.setMaxAge(MAX_AGE);
                    return configuration;
                }))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/h2-console/**",
                                        "/actuator/**"
                                ).permitAll()
                                // Allow all requests to the tasks API for this template
                                .requestMatchers("/tasks/**").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                // Disable frame options to allow H2 console to work
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(allowedOrigins);
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.OPTIONS.name(),
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name()));
        config.setMaxAge(MAX_AGE);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
