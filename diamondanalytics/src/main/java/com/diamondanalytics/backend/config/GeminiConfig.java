package com.diamondanalytics.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Bean(name = "geminiWebClient")
    public WebClient geminiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/" + model)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)) // 5MB
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }
}