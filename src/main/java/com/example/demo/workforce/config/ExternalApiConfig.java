package com.example.demo.workforce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ExternalApiConfig {
    @Bean
    public RestTemplate workforceRestTemplate(RestTemplateBuilder builder,
                                              @Value("${workforce.external-api.connect-timeout-ms:1000}") long connectTimeoutMs,
                                              @Value("${workforce.external-api.read-timeout-ms:2000}") long readTimeoutMs) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
