package com.hackathon.probability.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShipMobilityResponse(
		List<String> cities,
		List<ShipMobilityItem> ships
) {}
