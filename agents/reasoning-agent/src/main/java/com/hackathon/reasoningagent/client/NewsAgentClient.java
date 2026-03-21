package com.hackathon.reasoningagent.client;

import com.hackathon.reasoningagent.config.ReasoningUpstreamProperties;
import com.hackathon.reasoningagent.dto.news.AgentRunResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NewsAgentClient {

    private final RestClient restClient;
    private final ReasoningUpstreamProperties props;

    public NewsAgentClient(RestClient reasoningRestClient, ReasoningUpstreamProperties props) {
        this.restClient = reasoningRestClient;
        this.props = props;
    }

    public AgentRunResponse fetchClassifiedNews() {
        String uri = UriComponentsBuilder.fromUriString(props.newsAgentBaseUrl())
                .path("/api/agent/classified-news")
                .build()
                .toUriString();
        try {
            AgentRunResponse body = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(AgentRunResponse.class);
            if (body == null) {
                throw new UpstreamUnavailableException("news-agent returned empty body");
            }
            return body;
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("news-agent unreachable: " + e.getMessage(), e);
        }
    }
}
