package com.hackathon.reasoningagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reasoning.pipeline")
public record ReasoningPipelineProperties(
        /** Default radius when the client omits {@code radiusNm} (nautical miles). */
        double searchRadiusNm,
        /** Minimum allowed {@code radiusNm} query value (nautical miles). */
        double minRadiusNm,
        /** Maximum allowed {@code radiusNm} query value (nautical miles). */
        double maxRadiusNm
) {
}
