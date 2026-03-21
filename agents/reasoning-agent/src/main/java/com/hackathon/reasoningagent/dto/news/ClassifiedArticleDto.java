package com.hackathon.reasoningagent.dto.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClassifiedArticleDto(
        String uri,
        String title,
        String body,
        String url,
        String date,
        String dateTime,
        List<CategoryAssignmentDto> categories
) {
}
