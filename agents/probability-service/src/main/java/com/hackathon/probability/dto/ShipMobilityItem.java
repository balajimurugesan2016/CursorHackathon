package com.hackathon.probability.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShipMobilityItem(
		String shipName,
		String speed,
		List<String> cities
) {}
