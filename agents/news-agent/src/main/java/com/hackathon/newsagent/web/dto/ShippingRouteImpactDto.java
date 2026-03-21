package com.hackathon.newsagent.web.dto;

import java.util.List;

/**
 * Estimated likelihood (0–1) that the article materially concerns commercial shipping routes
 * (lanes, canals/straits, diversions, maritime risk, etc.), plus matched lexicon terms.
 */
public record ShippingRouteImpactDto(
        double probability,
        List<String> matchedSignals
) {
}
