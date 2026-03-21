package com.hackathon.reasoningagent.dto.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryAssignmentDto(
        String categoryId,
        String categoryLabel,
        String categoryDescription,
        double score,
        List<String> matchedSignals
) {
}
