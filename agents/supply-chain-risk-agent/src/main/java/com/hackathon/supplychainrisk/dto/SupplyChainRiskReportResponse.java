package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record SupplyChainRiskReportResponse(
        /** Max plant-level risk in the portfolio (0–1). */
        double portfolioRiskScore,
        String portfolioRationale,
        int reasoningArticleCount,
        double searchRadiusNm,
        int plantCount,
        List<PlantSupplyRiskDto> plants
) {
}
