package com.hackathon.newsagent.dto;

import java.util.List;

public record ClassifiedNewsResponse(
	int articleCount,
	List<ClassifiedArticleDto> articles
) {}
