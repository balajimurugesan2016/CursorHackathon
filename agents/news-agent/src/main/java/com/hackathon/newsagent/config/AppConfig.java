package com.hackathon.newsagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class AppConfig {

	@Bean
	public WebClient mockServicesWebClient(
			@Value("${news.mock-services-url:http://localhost:8082}") String baseUrl) {
		String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofSeconds(10));
		return WebClient.builder()
				.baseUrl(url)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}

	@Bean
	public WebClient locationServiceWebClient(
			@Value("${news.location-service-url:http://localhost:8095}") String baseUrl) {
		String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofSeconds(10));
		return WebClient.builder()
				.baseUrl(url)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}
}
