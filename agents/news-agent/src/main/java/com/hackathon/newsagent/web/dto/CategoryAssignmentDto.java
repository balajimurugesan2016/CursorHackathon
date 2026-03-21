package com.hackathon.newsagent.web.dto;

import java.util.List;

public record CategoryAssignmentDto(
        String categoryId,
        String categoryLabel,
        String categoryDescription,
        double score,
        List<String> matchedSignals
) {
}
