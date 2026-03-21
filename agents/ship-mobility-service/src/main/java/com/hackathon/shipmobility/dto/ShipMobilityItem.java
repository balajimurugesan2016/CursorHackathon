package com.hackathon.shipmobility.dto;

import java.util.List;

public record ShipMobilityItem(
		String shipName,
		String speed,
		List<String> cities
) {}
