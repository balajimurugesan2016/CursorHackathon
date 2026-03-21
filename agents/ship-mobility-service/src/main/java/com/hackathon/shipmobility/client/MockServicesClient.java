package com.hackathon.shipmobility.client;

import com.hackathon.shipmobility.dto.PlaceDto;
import com.hackathon.shipmobility.dto.VesselDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@Component
public class MockServicesClient {

	private final RestClient restClient;

	public MockServicesClient(@Qualifier("mockServicesRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public List<VesselDto> getVesselsByNames(Set<String> names) {
		if (names == null || names.isEmpty()) {
			return List.of();
		}
		String namesParam = String.join(",", names);
		List<VesselDto> result = restClient.get()
				.uri("/api/v1/vessels/by-names?names={names}", namesParam)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return result != null ? result : List.of();
	}

	public List<VesselDto> getVesselsByArea(double latitude, double longitude, double circleRadiusKm) {
		List<VesselDto> result = restClient.post()
				.uri("/api/vessels_operations/get-vessels-by-area?latitude={latitude}&longitude={longitude}&circle_radius={radius}",
						latitude, longitude, circleRadiusKm)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return result != null ? result : List.of();
	}

	public List<PlaceDto> getPlaces(double latitude, double longitude, double circleRadiusKm) {
		List<PlaceDto> result = restClient.post()
				.uri("/api/vessels_operations/get-places?latitude={latitude}&longitude={longitude}&circle_radius={radius}",
						latitude, longitude, circleRadiusKm)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return result != null ? result : List.of();
	}
}
