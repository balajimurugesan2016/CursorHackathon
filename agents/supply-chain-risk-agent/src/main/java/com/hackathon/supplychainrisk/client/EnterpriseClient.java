package com.hackathon.supplychainrisk.client;

import com.hackathon.supplychainrisk.dto.enterprise.EnterprisePlantDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class EnterpriseClient {

    private final RestClient restClient;

    public EnterpriseClient(@Qualifier("enterprise") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<EnterprisePlantDto> listPlants() {
        try {
            return restClient.get()
                    .uri("/api/v1/plants")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("Enterprise service: failed to list plants — " + e.getMessage(), e);
        }
    }

    public EnterprisePlantDto getPlant(long id) {
        try {
            return restClient.get()
                    .uri("/api/v1/plants/{id}", id)
                    .retrieve()
                    .body(EnterprisePlantDto.class);
        } catch (RestClientException e) {
            throw new UpstreamUnavailableException("Enterprise service: failed to load plant " + id + " — " + e.getMessage(), e);
        }
    }
}
