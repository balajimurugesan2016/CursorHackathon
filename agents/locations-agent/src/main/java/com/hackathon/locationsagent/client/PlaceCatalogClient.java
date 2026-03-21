package com.hackathon.locationsagent.client;

import com.hackathon.locationsagent.config.LocationsApiProperties;
import com.hackathon.locationsagent.model.PlaceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class PlaceCatalogClient {

    private final RestClient restClient;
    private final LocationsApiProperties props;

    public PlaceCatalogClient(RestClient locationsRestClient, LocationsApiProperties props) {
        this.restClient = locationsRestClient;
        this.props = props;
    }

    public List<PlaceDto> fetchCatalog() {
        try {
            PlaceDto[] arr = restClient.get()
                    .uri(props.catalogPath())
                    .retrieve()
                    .body(PlaceDto[].class);
            if (arr == null) {
                return List.of();
            }
            return List.of(arr);
        } catch (RestClientException e) {
            throw new PlaceCatalogUnavailableException(
                    "Could not load place catalog from " + props.baseUrl() + props.catalogPath() + ": " + e.getMessage(),
                    e
            );
        }
    }
}
