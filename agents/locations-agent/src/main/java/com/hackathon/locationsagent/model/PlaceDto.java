package com.hackathon.locationsagent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlaceDto(
        String name,
        String type,
        String latitude,
        String longitude
) {
}
