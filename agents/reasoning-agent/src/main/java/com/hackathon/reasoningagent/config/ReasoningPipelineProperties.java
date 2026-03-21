package com.hackathon.reasoningagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reasoning.pipeline")
public record ReasoningPipelineProperties(
        /** Default radius when the client omits {@code radiusKm}. */
        double searchRadiusKm,
        /** Minimum allowed {@code radiusKm} query value (km). */
        double minRadiusKm,
        /** Maximum allowed {@code radiusKm} query value (km). */
        double maxRadiusKm
) {
}
