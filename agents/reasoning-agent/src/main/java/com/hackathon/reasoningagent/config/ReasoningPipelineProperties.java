package com.hackathon.reasoningagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reasoning.pipeline")
public record ReasoningPipelineProperties(
        double searchRadiusKm
) {
}
