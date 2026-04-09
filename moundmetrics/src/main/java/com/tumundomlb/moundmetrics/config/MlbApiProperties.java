package com.tumundomlb.moundmetrics.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mlb.api")
@Getter
@Setter
public class MlbApiProperties {
    private String baseUrl = "https://statsapi.mlb.com/api/v1";
}