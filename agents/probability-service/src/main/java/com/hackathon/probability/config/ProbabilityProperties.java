package com.hackathon.probability.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "probability")
public record ProbabilityProperties(
		String newsAgentUrl,
		String shipMobilityUrl,
		int weightSpeedLow,
		int weightLocationMatch,
		int weightLocationAndSpeed,
		double speedThresholdKn,
		int maxScore,
		String gulfLocations,
		int gulfMinPercent
) {}
