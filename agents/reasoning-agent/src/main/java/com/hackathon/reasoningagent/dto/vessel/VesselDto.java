package com.hackathon.reasoningagent.dto.vessel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VesselDto(
        String mmsi,
        String name,
        String latitude,
        String longitude,
        String speed,
        String course,
        String heading
) {
}
