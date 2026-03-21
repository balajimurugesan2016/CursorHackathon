package com.hackathon.supplychainrisk.web;

import com.hackathon.supplychainrisk.client.UpstreamUnavailableException;
import com.hackathon.supplychainrisk.dto.SupplyChainRiskReportResponse;
import com.hackathon.supplychainrisk.service.SupplyChainRiskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class SupplyChainRiskController {

    private final SupplyChainRiskService supplyChainRiskService;

    public SupplyChainRiskController(SupplyChainRiskService supplyChainRiskService) {
        this.supplyChainRiskService = supplyChainRiskService;
    }

    /**
     * Loads plants and linked suppliers from the enterprise service, fetches the reasoning-agent report,
     * and estimates exposure-weighted risk using proximity to resolved news locations and text overlap.
     */
    @GetMapping(value = "/supply-chain-risk-report", produces = "application/json")
    public SupplyChainRiskReportResponse supplyChainRiskReport(
            @RequestParam(value = "radiusNm", required = false) Double radiusNm
    ) {
        return supplyChainRiskService.buildReport(radiusNm);
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ResponseEntity<ErrorBody> handleUpstream(UpstreamUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
