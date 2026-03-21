package com.hackathon.locationsagent.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({LocationsApiProperties.class, ResolveProperties.class})
public class AppConfig {

    @Bean
    RestClient locationsRestClient(LocationsApiProperties props) {
        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .build();
    }
}
