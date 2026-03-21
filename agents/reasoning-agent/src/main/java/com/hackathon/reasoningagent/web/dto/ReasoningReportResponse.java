package com.hackathon.reasoningagent.web.dto;

import java.util.List;

public record ReasoningReportResponse(
        int articleCount,
        List<ArticleReasoningDto> articles,
        /** Radius in km actually used for vessel-agent searches on this run. */
        double searchRadiusKm
) {
}
