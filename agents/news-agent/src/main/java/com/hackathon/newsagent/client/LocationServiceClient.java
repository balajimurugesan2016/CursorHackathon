package com.hackathon.newsagent.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class LocationServiceClient {

	private final WebClient webClient;

	public LocationServiceClient(@Qualifier("locationServiceWebClient") WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<List<String>> getCities() {
		return webClient.get()
				.uri("/api/location/cities")
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<>() {});
	}
}
