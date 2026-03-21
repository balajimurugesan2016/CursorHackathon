package com.enterpriseservice.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * DTO for deserializing plant records from mock_plants.json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MockPlantJson(
		String plantName,
		String location,
		String latitude,
		String longitude,
		String status,
		BigDecimal capacityPct,
		Integer totalLines,
		Integer linesActive
) {}
