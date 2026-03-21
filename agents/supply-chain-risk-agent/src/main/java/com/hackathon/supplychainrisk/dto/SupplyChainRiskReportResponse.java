package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record SupplyChainRiskReportResponse(
        /** Max plant-level risk in the portfolio (0–1). */
        double portfolioRiskScore,
        /** Max disturbance certainty across plants (category risk blended with vessel ETA imminence). */
        double portfolioDisturbanceCertainty,
        String portfolioRationale,
        /** Explains risk + maritime speed / ETA contribution. */
        String portfolioDisturbanceRationale,
        /** Soonest ETA (hours) portfolio-wide when speeds are available. */
        Double portfolioEstimatedHoursToImpact,
        int reasoningArticleCount,
        double searchRadiusNm,
        int plantCount,
        List<PlantSupplyRiskDto> plants
) {
}
