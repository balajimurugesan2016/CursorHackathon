package com.hackathon.reasoningagent.web;

import com.hackathon.reasoningagent.client.UpstreamUnavailableException;
import com.hackathon.reasoningagent.service.ReasoningPipelineService;
import com.hackathon.reasoningagent.web.dto.ReasoningReportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class ReasoningAgentController {

    private final ReasoningPipelineService pipelineService;

    public ReasoningAgentController(ReasoningPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    /**
     * Runs the pipeline: classified news → catalog mention scan → locations-agent resolution → vessel-agent search.
     */
    @GetMapping(value = "/reasoning-report", produces = "application/json")
    public ReasoningReportResponse reasoningReport() {
        return pipelineService.buildReport();
    }

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ResponseEntity<ErrorBody> handleUpstream(UpstreamUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
