package com.hackathon.reasoningagent.web.dto;

/**
 * Reasoning-layer supply-chain risk estimate for one news category, combining
 * the news-agent score with geography, vessel proximity, and route-impact context.
 */
public record CategoryRiskFactorDto(
        String categoryId,
        String categoryLabel,
        /** Normalized share from news classification (0–1). */
        double newsCategoryScore,
        /** Composite risk factor for this category after reasoning (0–1). */
        double riskFactor,
        /** Short explanation of how context adjusted the score. */
        String rationale
) {
}
