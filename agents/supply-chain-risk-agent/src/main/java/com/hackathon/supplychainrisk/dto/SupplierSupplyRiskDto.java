package com.hackathon.supplychainrisk.dto;

import java.util.List;

public record SupplierSupplyRiskDto(
        Long supplierId,
        String supplierName,
        /** Max composite category risk from reasoning articles that expose this supplier (0–1). */
        double riskScore,
        List<String> contributingArticleTitles,
        List<String> signals
) {
}
