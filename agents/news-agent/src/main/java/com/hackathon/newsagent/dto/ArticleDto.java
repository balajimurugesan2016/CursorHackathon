package com.hackathon.newsagent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticleDto(
	@JsonProperty("uri") String uri,
	@JsonProperty("title") String title,
	@JsonProperty("body") String body,
	@JsonProperty("date") String date,
	@JsonProperty("url") String url
) {}
