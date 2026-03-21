package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record PlantSupplyRiskDto(
        Long plantId,
        String plantName,
        String location,
        /** Max risk affecting this plant (direct geo/text hit on the plant or any linked supplier). */
        double plantRiskScore,
        /** Max disturbance certainty (risk × maritime imminence) for this plant or its suppliers. */
        double disturbanceCertainty,
        /** Soonest ETA (hours) among contributing exposures, when vessel speeds allow. */
        Double estimatedHoursToImpact,
        String rationale,
        List<SupplierSupplyRiskDto> suppliers
) {
}
