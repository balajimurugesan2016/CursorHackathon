package com.hackathon.supplychainrisk.dto.reasoning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReasoningReportResponse(
        int articleCount,
        List<ArticleReasoningDto> articles,
        double searchRadiusNm
) {
}
