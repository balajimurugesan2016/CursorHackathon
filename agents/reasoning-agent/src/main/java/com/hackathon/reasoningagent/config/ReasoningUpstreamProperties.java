package com.hackathon.reasoningagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reasoning.upstream")
public record ReasoningUpstreamProperties(
        String newsAgentBaseUrl,
        String locationsAgentBaseUrl,
        String vesselAgentBaseUrl,
        String placesCatalogUrl
) {
}
