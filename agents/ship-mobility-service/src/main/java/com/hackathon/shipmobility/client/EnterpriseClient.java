package com.hackathon.shipmobility.client;

import com.hackathon.shipmobility.dto.ShipmentDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class EnterpriseClient {

	private final RestClient restClient;

	public EnterpriseClient(@Qualifier("enterpriseRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public List<ShipmentDto> listShipments() {
		List<ShipmentDto> result = restClient.get()
				.uri("/api/v1/shipments")
				.retrieve()
				.body(new ParameterizedTypeReference<>() {});
		return result != null ? result : List.of();
	}
}
