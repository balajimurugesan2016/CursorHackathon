package com.hackathon.reasoningagent.dto.locations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResolvedLocationDto(
        String query,
        String matchedName,
        String placeType,
        double latitude,
        double longitude,
        String matchKind,
        double confidence
) {
}
