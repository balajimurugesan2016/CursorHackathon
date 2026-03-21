package com.hackathon.supplychainrisk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supplychain-risk")
public record SupplyChainRiskProperties(
        String enterpriseBaseUrl,
        String reasoningBaseUrl,
        double proximityRadiusKm,
        int httpTimeoutMs
) {
}
