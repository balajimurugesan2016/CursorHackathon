package com.hackathon.supplychainrisk.service;

import com.hackathon.supplychainrisk.client.EnterpriseClient;
import com.hackathon.supplychainrisk.client.ReasoningClient;
import com.hackathon.supplychainrisk.config.SupplyChainRiskProperties;
import com.hackathon.supplychainrisk.dto.SupplyChainRiskReportResponse;
import com.hackathon.supplychainrisk.dto.enterprise.EnterprisePlantDto;
import com.hackathon.supplychainrisk.dto.reasoning.ReasoningReportResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupplyChainRiskService {

    private final EnterpriseClient enterpriseClient;
    private final ReasoningClient reasoningClient;
    private final SupplyChainRiskAnalyzer analyzer;
    private final SupplyChainRiskProperties properties;

    public SupplyChainRiskService(
            EnterpriseClient enterpriseClient,
            ReasoningClient reasoningClient,
            SupplyChainRiskAnalyzer analyzer,
            SupplyChainRiskProperties properties
    ) {
        this.enterpriseClient = enterpriseClient;
        this.reasoningClient = reasoningClient;
        this.analyzer = analyzer;
        this.properties = properties;
    }

    public SupplyChainRiskReportResponse buildReport(Double radiusNm) {
        ReasoningReportResponse reasoning = reasoningClient.fetchReasoningReport(radiusNm);
        List<EnterprisePlantDto> summaries = enterpriseClient.listPlants();
        if (summaries == null) {
            summaries = List.of();
        }
        List<EnterprisePlantDto> details = new ArrayList<>();
        for (EnterprisePlantDto p : summaries) {
            if (p.id() == null) {
                continue;
            }
            details.add(enterpriseClient.getPlant(p.id()));
        }
        return analyzer.analyze(details, reasoning, properties.proximityRadiusKm());
    }
}
