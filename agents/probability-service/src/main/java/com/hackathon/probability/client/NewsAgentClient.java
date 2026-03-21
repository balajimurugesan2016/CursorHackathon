package com.hackathon.probability.client;

import com.hackathon.probability.dto.ClassifiedNewsResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NewsAgentClient {

	private final RestClient restClient;

	public NewsAgentClient(@Qualifier("newsAgentRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public ClassifiedNewsResponse getClassifiedNews() {
		return restClient.get()
				.uri("/api/agent/classified-news")
				.retrieve()
				.body(ClassifiedNewsResponse.class);
	}
}
