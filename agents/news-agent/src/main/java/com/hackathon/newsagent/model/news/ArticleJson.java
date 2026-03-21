package com.hackathon.newsagent.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticleJson(
        String uri,
        String title,
        String body,
        String url,
        String date,
        String dateTime
) {
}
