package com.hackathon.locationsagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "locations.api")
public record LocationsApiProperties(
        String baseUrl,
        String catalogPath
) {
}
