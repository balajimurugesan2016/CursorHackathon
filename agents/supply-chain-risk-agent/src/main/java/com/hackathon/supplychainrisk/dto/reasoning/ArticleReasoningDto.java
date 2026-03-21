package com.hackathon.supplychainrisk.dto.reasoning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticleReasoningDto(
        ClassifiedArticleDto classified,
        List<CategoryRiskFactorDto> categoryRisks,
        List<String> catalogMentions,
        List<ResolvedLocationDto> resolvedLocations,
        List<VesselNearLocationDto> vesselsNearLocations
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VesselNearLocationDto(
            String anchorMatchedName,
            double latitude,
            double longitude,
            double radiusNm,
            int vesselCount,
            List<VesselDto> vessels
    ) {
    }
}
