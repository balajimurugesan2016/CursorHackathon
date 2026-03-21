package com.hackathon.newsagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent.classification")
public record ClassificationProperties(
        double minScore,
        int maxCategoriesPerArticle
) {
}
