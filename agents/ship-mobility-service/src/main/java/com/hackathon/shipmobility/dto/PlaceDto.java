package com.hackathon.shipmobility.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlaceDto(
		String name,
		String type,
		String latitude,
		String longitude
) {}
