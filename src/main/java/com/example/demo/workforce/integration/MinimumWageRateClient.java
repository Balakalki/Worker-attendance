package com.example.demo.workforce.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class MinimumWageRateClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MinimumWageRateClient.class);

    private final RestTemplate restTemplate;
    private final String minimumWageApiUrl;

    public MinimumWageRateClient(RestTemplate workforceRestTemplate,
                                 @Value("${workforce.minimum-wage-api.url:}") String minimumWageApiUrl) {
        this.restTemplate = workforceRestTemplate;
        this.minimumWageApiUrl = minimumWageApiUrl;
    }

    public Optional<BigDecimal> fetchLatestRate() {
        if (!StringUtils.hasText(minimumWageApiUrl)) {
            return Optional.empty();
        }

        try {
            BigDecimal rate = restTemplate.getForObject(minimumWageApiUrl, BigDecimal.class);
            return Optional.ofNullable(rate);
        } catch (RuntimeException exception) {
            LOGGER.warn("Minimum wage API unavailable; continuing with stored overtime data", exception);
            return Optional.empty();
        }
    }
}
