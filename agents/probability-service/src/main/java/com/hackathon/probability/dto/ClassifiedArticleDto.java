package com.hackathon.probability.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClassifiedArticleDto(
		String uri,
		String title,
		List<String> locations,
		List<String> topics
) {}
