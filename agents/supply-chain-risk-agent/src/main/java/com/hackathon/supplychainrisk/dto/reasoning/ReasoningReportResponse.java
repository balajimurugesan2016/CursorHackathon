package com.hackathon.supplychainrisk.dto.reasoning;

import java.util.List;

public record ReasoningReportResponse(
        int articleCount,
        List<ArticleReasoningDto> articles,
        double searchRadiusNm
) {
}
