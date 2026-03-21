package com.hackathon.newsagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "news.api")
public record NewsApiProperties(
        String baseUrl,
        String path
) {
}
