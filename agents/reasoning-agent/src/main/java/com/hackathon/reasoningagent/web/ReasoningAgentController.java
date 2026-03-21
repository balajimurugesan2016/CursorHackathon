package com.hackathon.reasoningagent.web;

import com.hackathon.reasoningagent.client.UpstreamUnavailableException;
import com.hackathon.reasoningagent.config.ReasoningPipelineProperties;
import com.hackathon.reasoningagent.service.ReasoningPipelineService;
import com.hackathon.reasoningagent.web.dto.ReasoningReportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/agent")
public class ReasoningAgentController {

    private final ReasoningPipelineService pipelineService;
    private final ReasoningPipelineProperties pipelineProperties;

    public ReasoningAgentController(
            ReasoningPipelineService pipelineService,
            ReasoningPipelineProperties pipelineProperties
    ) {
        this.pipelineService = pipelineService;
        this.pipelineProperties = pipelineProperties;
    }

    /**
     * Runs the pipeline: classified news → catalog mention scan → locations-agent resolution → vessel-agent search.
     *
     * @param radiusNm optional search radius in <strong>nautical miles</strong> for vessel lookups
     *                 (defaults to {@code reasoning.pipeline.search-radius-nm})
     */
    @GetMapping(value = "/reasoning-report", produces = "application/json")
    public ReasoningReportResponse reasoningReport(
            @RequestParam(value = "radiusNm", required = false) Double radiusNm
    ) {
        double radius = radiusNm != null ? radiusNm : pipelineProperties.searchRadiusNm();
        validateRadius(radius);
        return pipelineService.buildReport(radius);
    }

    private void validateRadius(double radius) {
        if (Double.isNaN(radius) || Double.isInfinite(radius)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radiusNm must be a finite number");
        }
        double min = pipelineProperties.minRadiusNm();
        double max = pipelineProperties.maxRadiusNm();
        if (radius < min || radius > max) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "radiusNm must be between " + min + " and " + max
            );
        }
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ResponseEntity<ErrorBody> handleUpstream(UpstreamUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
