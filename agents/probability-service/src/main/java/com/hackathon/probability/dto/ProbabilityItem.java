package com.hackathon.probability.dto;

import java.util.List;

public record ProbabilityItem(
		String title,
		List<String> locations,
		int probabilityPercent
) {}
