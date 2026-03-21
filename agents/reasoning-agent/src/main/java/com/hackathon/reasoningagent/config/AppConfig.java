package com.hackathon.reasoningagent.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({ReasoningUpstreamProperties.class, ReasoningPipelineProperties.class})
public class AppConfig {

    @Bean
    RestClient reasoningRestClient() {
        return RestClient.builder().build();
    }
}
