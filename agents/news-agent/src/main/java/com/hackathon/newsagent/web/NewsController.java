package com.hackathon.newsagent.web;

import com.hackathon.newsagent.dto.ClassifiedNewsResponse;
import com.hackathon.newsagent.service.NewsClassificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class NewsController {

	private final NewsClassificationService newsClassificationService;

	public NewsController(NewsClassificationService newsClassificationService) {
		this.newsClassificationService = newsClassificationService;
	}

	@GetMapping(value = "/classified-news", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getClassifiedNews() {
		try {
			ClassifiedNewsResponse response = newsClassificationService.getClassifiedNews();
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(503).body(Map.of("message", e.getMessage()));
		}
	}
}
