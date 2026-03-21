package com.hackathon.newsagent.client;

import com.hackathon.newsagent.dto.ArticlesResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MockServicesClient {

	private final WebClient webClient;

	public MockServicesClient(@Qualifier("mockServicesWebClient") WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<ArticlesResponseDto> getArticles() {
		return webClient.post()
				.uri("/api/v1/article/getArticles")
				.retrieve()
				.bodyToMono(ArticlesResponseDto.class);
	}
}
