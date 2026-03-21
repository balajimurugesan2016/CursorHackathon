package com.hackathon.newsagent.dto;

import java.util.List;

public record ClassifiedArticleDto(
	String uri,
	String title,
	List<String> locations,
	List<String> topics
) {}
