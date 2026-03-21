package com.hackathon.newsagent.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NewsApiResponseEnvelope(
        ArticlesInner articles
) {
}
