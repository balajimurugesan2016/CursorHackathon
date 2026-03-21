package com.hackathon.shipmobility.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ship-mobility")
public record ShipMobilityProperties(
		int nearbyRadiusKm
) {
	public ShipMobilityProperties() {
		this(200);
	}
}
