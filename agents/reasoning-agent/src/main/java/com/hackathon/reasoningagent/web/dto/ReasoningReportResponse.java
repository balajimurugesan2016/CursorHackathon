package com.hackathon.reasoningagent.web.dto;

import java.util.List;

public record ReasoningReportResponse(
        int articleCount,
        List<ArticleReasoningDto> articles
) {
}
