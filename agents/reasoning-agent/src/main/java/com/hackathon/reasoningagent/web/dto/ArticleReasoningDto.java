package com.hackathon.reasoningagent.web.dto;

import com.hackathon.reasoningagent.dto.locations.ResolvedLocationDto;
import com.hackathon.reasoningagent.dto.news.ClassifiedArticleDto;
import com.hackathon.reasoningagent.dto.vessel.VesselDto;

import java.util.List;

public record ArticleReasoningDto(
        ClassifiedArticleDto classified,
        List<String> catalogMentions,
        List<ResolvedLocationDto> resolvedLocations,
        List<VesselNearLocationDto> vesselsNearLocations
) {
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
