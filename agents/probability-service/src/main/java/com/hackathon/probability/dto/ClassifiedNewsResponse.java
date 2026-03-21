package com.hackathon.probability.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClassifiedNewsResponse(
		int articleCount,
		List<ClassifiedArticleDto> articles
) {}
