package com.hackathon.reasoningagent.client;

import com.hackathon.reasoningagent.config.ReasoningUpstreamProperties;
import com.hackathon.reasoningagent.dto.locations.ResolvedLocationDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
public class LocationsAgentClient {

    private final RestClient restClient;
    private final ReasoningUpstreamProperties props;

    public LocationsAgentClient(RestClient reasoningRestClient, ReasoningUpstreamProperties props) {
        this.restClient = reasoningRestClient;
        this.props = props;
    }

    public Optional<ResolvedLocationDto> resolveLocationName(String placeName) {
        String uri = UriComponentsBuilder.fromUriString(props.locationsAgentBaseUrl())
                .path("/api/agent/resolve-location")
                .queryParam("name", placeName)
                .build(false)
                .toUriString();
        try {
            ResolvedLocationDto body = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(ResolvedLocationDto.class);
            return Optional.ofNullable(body);
        } catch (RestClientResponseException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                return Optional.empty();
            }
            throw new UpstreamUnavailableException("locations-agent error: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("locations-agent unreachable: " + e.getMessage(), e);
        }
    }
}
