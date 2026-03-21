package com.hackathon.vesselagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vessels.api")
public record VesselsApiProperties(
        String baseUrl,
        String path
) {
}
