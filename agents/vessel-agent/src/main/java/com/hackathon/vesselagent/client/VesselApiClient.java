package com.hackathon.vesselagent.client;

import com.hackathon.vesselagent.config.VesselsApiProperties;
import com.hackathon.vesselagent.model.VesselDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class VesselApiClient {

    private final RestClient restClient;
    private final VesselsApiProperties props;

    public VesselApiClient(RestClient vesselsRestClient, VesselsApiProperties props) {
        this.restClient = vesselsRestClient;
        this.props = props;
    }

    /**
     * Calls the mock service with all three parameters so filtering uses the Haversine radius (km).
     */
    public List<VesselDto> fetchVesselsInRadius(double latitude, double longitude, double radiusKm) {
        try {
            VesselDto[] arr = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(props.path())
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("circle_radius", radiusKm)
                            .build())
                    .retrieve()
                    .body(VesselDto[].class);
            if (arr == null) {
                return List.of();
            }
            return List.of(arr);
        } catch (RestClientException e) {
            throw new VesselApiUnavailableException(
                    "Could not reach vessel API at " + props.baseUrl() + props.path() + ": " + e.getMessage(),
                    e
            );
        }
    }
}
