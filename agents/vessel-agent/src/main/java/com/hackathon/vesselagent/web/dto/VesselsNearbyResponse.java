package com.hackathon.vesselagent.web.dto;

import com.hackathon.vesselagent.model.VesselDto;

import java.util.List;

public record VesselsNearbyResponse(
        double latitude,
        double longitude,
        double radiusNm,
        int vesselCount,
        List<VesselDto> vessels
) {
}
