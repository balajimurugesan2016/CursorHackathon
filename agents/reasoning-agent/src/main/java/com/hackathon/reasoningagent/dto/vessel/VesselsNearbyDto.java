package com.hackathon.reasoningagent.dto.vessel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VesselsNearbyDto(
        double latitude,
        double longitude,
        /** vessel-agent returns {@code radiusKm}; value is not used by the pipeline (request radius is used for NM). */
        @JsonProperty("radiusKm")
        double radiusKm,
        int vesselCount,
        List<VesselDto> vessels
) {
}
