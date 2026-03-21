package com.hackathon.probability.client;

import com.hackathon.probability.dto.ShipMobilityResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ShipMobilityClient {

	private final RestClient restClient;

	public ShipMobilityClient(@Qualifier("shipMobilityRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public ShipMobilityResponse getShipMobility() {
		return restClient.get()
				.uri("/api/ship-mobility")
				.retrieve()
				.body(ShipMobilityResponse.class);
	}
}
