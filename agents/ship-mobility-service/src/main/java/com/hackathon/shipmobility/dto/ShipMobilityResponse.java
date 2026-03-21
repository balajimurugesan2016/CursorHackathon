package com.hackathon.shipmobility.dto;

import java.util.List;

public record ShipMobilityResponse(
		List<String> cities,
		List<ShipMobilityItem> ships
) {}
