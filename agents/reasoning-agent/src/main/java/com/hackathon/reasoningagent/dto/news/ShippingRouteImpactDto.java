package com.hackathon.reasoningagent.dto.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ShippingRouteImpactDto(
        double probability,
        List<String> matchedSignals
) {
}
