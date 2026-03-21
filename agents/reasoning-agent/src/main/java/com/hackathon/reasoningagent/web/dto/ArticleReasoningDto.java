package com.hackathon.reasoningagent.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hackathon.reasoningagent.dto.locations.ResolvedLocationDto;
import com.hackathon.reasoningagent.dto.news.ClassifiedArticleDto;
import com.hackathon.reasoningagent.dto.vessel.VesselDto;

import java.util.List;

public record ArticleReasoningDto(
        ClassifiedArticleDto classified,
        /** Per-category composite supply-chain risk after reasoning (news score + context). */
        @JsonInclude(JsonInclude.Include.ALWAYS)
        List<CategoryRiskFactorDto> categoryRisks,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        List<String> catalogMentions,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        List<ResolvedLocationDto> resolvedLocations,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        List<VesselNearLocationDto> vesselsNearLocations
) {
    public record VesselNearLocationDto(
            String anchorMatchedName,
            double latitude,
            double longitude,
            double radiusNm,
            int vesselCount,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            List<VesselDto> vessels
    ) {
    }
}
