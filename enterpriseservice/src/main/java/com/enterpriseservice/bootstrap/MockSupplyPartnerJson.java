package com.enterpriseservice.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MockSupplyPartnerJson(
        String supplierName,
        String location,
        String latitude,
        String longitude,
        String contractStatus
) {
}
