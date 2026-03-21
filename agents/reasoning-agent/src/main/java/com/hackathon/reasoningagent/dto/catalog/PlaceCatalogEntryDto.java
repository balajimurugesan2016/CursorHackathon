package com.hackathon.reasoningagent.dto.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlaceCatalogEntryDto(
        String name,
        String type,
        String latitude,
        String longitude
) {
}
