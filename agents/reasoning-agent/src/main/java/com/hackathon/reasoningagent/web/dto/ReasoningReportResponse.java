package com.hackathon.reasoningagent.web.dto;

import java.util.List;

public record ReasoningReportResponse(
        int articleCount,
        List<ArticleReasoningDto> articles,
        /** Radius in nautical miles actually used for vessel-agent searches on this run. */
        double searchRadiusNm
) {
}
