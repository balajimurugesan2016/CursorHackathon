package com.hackathon.probability.dto;

import java.util.List;

public record ProbabilityResponse(
		int articleCount,
		List<ProbabilityItem> items
) {}
