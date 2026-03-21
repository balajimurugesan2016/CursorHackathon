package com.hackathon.supplychainrisk.dto.reasoning;

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
