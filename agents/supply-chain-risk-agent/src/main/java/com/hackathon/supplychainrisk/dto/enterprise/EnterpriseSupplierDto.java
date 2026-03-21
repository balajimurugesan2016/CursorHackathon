package com.hackathon.supplychainrisk.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EnterpriseSupplierDto(
        Long id,
        String supplierName,
        String location,
        String latitude,
        String longitude
) {
}
