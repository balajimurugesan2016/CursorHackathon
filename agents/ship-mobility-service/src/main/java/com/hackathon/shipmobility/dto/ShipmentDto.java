package com.hackathon.shipmobility.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShipmentDto(
		Long id,
		String shipNumber,
		String status
) {}
