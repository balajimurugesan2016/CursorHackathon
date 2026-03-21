package com.hackathon.newsagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticlesResponseDto(
	@JsonProperty("articles") ArticlesWrapper articles
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ArticlesWrapper(
		@JsonProperty("results") List<ArticleDto> results
	) {}
}
