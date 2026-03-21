package com.hackathon.newsagent.web;

import com.hackathon.newsagent.client.NewsApiUnavailableException;
import com.hackathon.newsagent.service.NewsClassificationService;
import com.hackathon.newsagent.web.dto.AgentRunResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final NewsClassificationService classificationService;

    public AgentController(NewsClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    /**
     * Fetches articles from the configured news API and returns multi-label supply-chain classifications.
     */
    @GetMapping(value = "/classified-news", produces = "application/json")
    public AgentRunResponse classifiedNews() {
        return classificationService.runAgent();
    }

    @ExceptionHandler(NewsApiUnavailableException.class)
    public ResponseEntity<ErrorBody> handleNewsDown(NewsApiUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorBody(ex.getMessage()));
    }

    public record ErrorBody(String message) {
    }
}
