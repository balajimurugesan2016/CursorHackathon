package com.hackathon.reasoningagent.dto.vessel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VesselsNearbyDto(
        double latitude,
        double longitude,
        double radiusKm,
        int vesselCount,
        List<VesselDto> vessels
) {
}
