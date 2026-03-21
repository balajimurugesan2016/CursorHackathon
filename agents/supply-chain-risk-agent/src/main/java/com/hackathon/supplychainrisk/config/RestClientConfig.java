package com.hackathon.supplychainrisk.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("enterprise")
    public RestClient enterpriseRestClient(SupplyChainRiskProperties props) {
        return baseBuilder(props).baseUrl(props.enterpriseBaseUrl()).build();
    }

    @Bean
    @Qualifier("reasoning")
    public RestClient reasoningRestClient(SupplyChainRiskProperties props) {
        return baseBuilder(props).baseUrl(props.reasoningBaseUrl()).build();
    }

    private static RestClient.Builder baseBuilder(SupplyChainRiskProperties props) {
        int ms = Math.max(1000, props.httpTimeoutMs());
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(ms))
                .build();
        JdkClientHttpRequestFactory rf = new JdkClientHttpRequestFactory(http);
        rf.setReadTimeout(Duration.ofMillis(ms));
        return RestClient.builder().requestFactory(rf);
    }
}
