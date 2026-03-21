package com.hackathon.newsagent.client;

import com.hackathon.newsagent.config.NewsApiProperties;
import com.hackathon.newsagent.model.news.ArticleJson;
import com.hackathon.newsagent.model.news.NewsApiResponseEnvelope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class NewsApiClient {

    private final RestClient restClient;
    private final NewsApiProperties props;

    public NewsApiClient(RestClient newsRestClient, NewsApiProperties props) {
        this.restClient = newsRestClient;
        this.props = props;
    }

    public List<ArticleJson> fetchArticles() {
        try {
            NewsApiResponseEnvelope envelope = restClient.post()
                    .uri(props.path())
                    .retrieve()
                    .body(NewsApiResponseEnvelope.class);
            if (envelope == null || envelope.articles() == null || envelope.articles().results() == null) {
                return List.of();
            }
            return envelope.articles().results();
        } catch (RestClientException e) {
            throw new NewsApiUnavailableException(
                    "Could not reach news API at " + props.baseUrl() + props.path() + ": " + e.getMessage(),
                    e
            );
        }
    }
}
