package com.hackathon.probability.web;

import com.hackathon.probability.dto.ProbabilityResponse;
import com.hackathon.probability.service.ProbabilityService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/probability")
public class ProbabilityController {

	private final ProbabilityService probabilityService;

	public ProbabilityController(ProbabilityService probabilityService) {
		this.probabilityService = probabilityService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getProbabilities() {
		try {
			ProbabilityResponse response = probabilityService.getProbabilities();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(503).body(Map.of("message", e.getMessage()));
		}
	}
}
