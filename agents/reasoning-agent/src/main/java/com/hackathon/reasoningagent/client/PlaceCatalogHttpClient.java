package com.hackathon.reasoningagent.client;

import com.hackathon.reasoningagent.config.ReasoningUpstreamProperties;
import com.hackathon.reasoningagent.dto.catalog.PlaceCatalogEntryDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class PlaceCatalogHttpClient {

    private final RestClient restClient;
    private final ReasoningUpstreamProperties props;

    public PlaceCatalogHttpClient(RestClient reasoningRestClient, ReasoningUpstreamProperties props) {
        this.restClient = reasoningRestClient;
        this.props = props;
    }

    public List<PlaceCatalogEntryDto> fetchCatalog() {
        try {
            PlaceCatalogEntryDto[] arr = restClient.get()
                    .uri(props.placesCatalogUrl())
                    .retrieve()
                    .body(PlaceCatalogEntryDto[].class);
            if (arr == null) {
                return List.of();
            }
            return List.of(arr);
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("place catalog unreachable: " + e.getMessage(), e);
        }
    }
}
