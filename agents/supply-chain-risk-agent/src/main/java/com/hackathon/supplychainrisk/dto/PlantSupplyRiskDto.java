package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record PlantSupplyRiskDto(
        Long plantId,
        String plantName,
        String location,
        /** Max risk affecting this plant (direct geo/text hit on the plant or any linked supplier). */
        double plantRiskScore,
        String rationale,
        List<SupplierSupplyRiskDto> suppliers
) {
}
