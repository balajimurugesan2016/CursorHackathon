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
    /** Reasoning JSON may omit empty lists; Jackson passes null for missing properties. */
    public ArticleReasoningDto {
        categoryRisks = categoryRisks != null ? categoryRisks : List.of();
        catalogMentions = catalogMentions != null ? catalogMentions : List.of();
        resolvedLocations = resolvedLocations != null ? resolvedLocations : List.of();
        vesselsNearLocations = vesselsNearLocations != null ? vesselsNearLocations : List.of();
    }

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
