package com.hackathon.supplychainrisk.client;

import com.hackathon.supplychainrisk.dto.reasoning.ReasoningReportResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

@Service
public class ReasoningClient {

    private final RestClient restClient;

    public ReasoningClient(@Qualifier("reasoning") RestClient restClient) {
        this.restClient = restClient;
    }

    public ReasoningReportResponse fetchReasoningReport(Double radiusNm) {
        try {
            return restClient.get()
                    .uri((UriBuilder b) -> {
                        var r = b.path("/api/agent/reasoning-report");
                        if (radiusNm != null) {
                            r.queryParam("radiusNm", radiusNm);
                        }
                        return r.build();
                    })
                    .retrieve()
                    .body(ReasoningReportResponse.class);
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("Reasoning agent: failed to load report — " + e.getMessage(), e);
        }
    }
}
