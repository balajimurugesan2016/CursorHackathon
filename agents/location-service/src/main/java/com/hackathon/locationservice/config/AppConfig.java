package com.hackathon.locationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(LocationServiceProperties.class)
public class AppConfig {

	@Bean
	public RestClient enterpriseRestClient(
			@Value("${ENTERPRISE_SERVICE_URL:http://localhost:8085}") String enterpriseBaseUrl) {
		String baseUrl = enterpriseBaseUrl.endsWith("/") ? enterpriseBaseUrl.substring(0, enterpriseBaseUrl.length() - 1) : enterpriseBaseUrl;
		Duration timeout = Duration.ofSeconds(10);
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(timeout);
		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.build();
	}

	@Bean
	public RestClient mockServicesRestClient(
			@Value("${MOCK_SERVICES_URL:http://localhost:8082}") String mockServicesBaseUrl) {
		String baseUrl = mockServicesBaseUrl.endsWith("/") ? mockServicesBaseUrl.substring(0, mockServicesBaseUrl.length() - 1) : mockServicesBaseUrl;
		Duration timeout = Duration.ofSeconds(10);
		HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(timeout);
		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.build();
	}
}
