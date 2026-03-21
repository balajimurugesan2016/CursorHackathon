package com.hackathon.vesselagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vessels.search")
public record VesselSearchProperties(
        double defaultRadiusNm
) {
}
