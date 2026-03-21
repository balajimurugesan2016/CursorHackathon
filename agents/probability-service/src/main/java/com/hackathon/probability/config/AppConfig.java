package com.hackathon.probability.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(ProbabilityProperties.class)
public class AppConfig {

	@Bean
	public RestClient newsAgentRestClient(ProbabilityProperties properties) {
		String baseUrl = normalizeBaseUrl(properties.newsAgentUrl());
		Duration timeout = Duration.ofSeconds(15);
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(timeout);
		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.build();
	}

	@Bean
	public RestClient shipMobilityRestClient(ProbabilityProperties properties) {
		String baseUrl = normalizeBaseUrl(properties.shipMobilityUrl());
		Duration timeout = Duration.ofSeconds(15);
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(timeout);
		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.build();
	}

	private static String normalizeBaseUrl(String url) {
		return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
	}
}
