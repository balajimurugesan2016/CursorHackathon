package com.hackathon.locationsagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "locations.resolve")
public record ResolveProperties(
        double minConfidence
) {
}
