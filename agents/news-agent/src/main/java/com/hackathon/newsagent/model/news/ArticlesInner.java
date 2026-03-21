package com.hackathon.newsagent.model.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticlesInner(
        int count,
        int page,
        int pages,
        int totalResults,
        List<ArticleJson> results
) {
}
