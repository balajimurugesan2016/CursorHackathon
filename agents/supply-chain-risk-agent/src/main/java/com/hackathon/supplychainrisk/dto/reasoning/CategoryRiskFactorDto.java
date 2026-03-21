package com.hackathon.supplychainrisk.dto.reasoning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryRiskFactorDto(
        String categoryId,
        String categoryLabel,
        double newsCategoryScore,
        double riskFactor,
        String rationale
) {
}
