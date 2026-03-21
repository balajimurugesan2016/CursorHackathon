package com.hackathon.supplychainrisk.dto.enterprise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EnterprisePlantDto(
        Long id,
        String plantName,
        String location,
        String latitude,
        String longitude,
        List<EnterpriseSupplierDto> suppliers
) {
}
