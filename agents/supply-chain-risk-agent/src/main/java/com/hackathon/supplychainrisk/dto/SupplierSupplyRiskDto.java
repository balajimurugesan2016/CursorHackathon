package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record SupplierSupplyRiskDto(
        Long supplierId,
        String supplierName,
        /** Max composite category risk from reasoning articles that expose this supplier (0–1). */
        double riskScore,
        /**
         * Confidence (0–1) that inbound maritime traffic will materially disturb this supplier soon,
         * combining category risk with vessel speed / proximity (ETA-style).
         */
        double disturbanceCertainty,
        /** Shortest estimated time (hours) for a tracked vessel to reach this supplier, if speed data allows. */
        Double estimatedHoursToImpact,
        List<String> contributingArticleTitles,
        List<String> signals
) {
}
