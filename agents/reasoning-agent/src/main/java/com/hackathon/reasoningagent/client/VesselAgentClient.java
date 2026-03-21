package com.hackathon.reasoningagent.client;

import com.hackathon.reasoningagent.config.ReasoningUpstreamProperties;
import com.hackathon.reasoningagent.dto.vessel.VesselsNearbyDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VesselAgentClient {

    private final RestClient restClient;
    private final ReasoningUpstreamProperties props;

    public VesselAgentClient(RestClient reasoningRestClient, ReasoningUpstreamProperties props) {
        this.restClient = reasoningRestClient;
        this.props = props;
    }

    public VesselsNearbyDto fetchVesselsNearby(double latitude, double longitude, double radiusKm) {
        String uri = UriComponentsBuilder.fromUriString(props.vesselAgentBaseUrl())
                .path("/api/agent/vessels-nearby")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("radiusKm", radiusKm)
                .build()
                .toUriString();
        try {
            VesselsNearbyDto body = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(VesselsNearbyDto.class);
            if (body == null) {
                throw new UpstreamUnavailableException("vessel-agent returned empty body");
            }
            return body;
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("vessel-agent unreachable: " + e.getMessage(), e);
        }
    }
}
