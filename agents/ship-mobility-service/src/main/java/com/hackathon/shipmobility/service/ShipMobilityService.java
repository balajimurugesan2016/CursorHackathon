package com.hackathon.shipmobility.service;

import com.hackathon.shipmobility.client.EnterpriseClient;
import com.hackathon.shipmobility.client.MockServicesClient;
import com.hackathon.shipmobility.config.ShipMobilityProperties;
import com.hackathon.shipmobility.dto.PlaceDto;
import com.hackathon.shipmobility.dto.ShipMobilityItem;
import com.hackathon.shipmobility.dto.ShipMobilityResponse;
import com.hackathon.shipmobility.dto.ShipmentDto;
import com.hackathon.shipmobility.dto.VesselDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShipMobilityService {

	private final EnterpriseClient enterpriseClient;
	private final MockServicesClient mockServicesClient;
	private final int nearbyRadiusKm;

	public ShipMobilityService(EnterpriseClient enterpriseClient,
			MockServicesClient mockServicesClient,
			ShipMobilityProperties properties) {
		this.enterpriseClient = enterpriseClient;
		this.mockServicesClient = mockServicesClient;
		this.nearbyRadiusKm = properties.nearbyRadiusKm();
	}

	public ShipMobilityResponse getShipMobility() {
		List<ShipmentDto> shipments = enterpriseClient.listShipments();

		Set<String> shipNames = shipments.stream()
				.filter(s -> s.status() == null || !"DELIVERED".equalsIgnoreCase(s.status()))
				.map(ShipmentDto::shipNumber)
				.filter(n -> n != null && !n.isBlank())
				.collect(Collectors.toSet());

		if (shipNames.isEmpty()) {
			return new ShipMobilityResponse(List.of(), List.of());
		}

		List<VesselDto> vessels = mockServicesClient.getVesselsByNames(shipNames);
		Set<String> allCities = new LinkedHashSet<>();
		List<ShipMobilityItem> shipItems = new ArrayList<>();

		for (VesselDto vessel : vessels) {
			double lat = parseDouble(vessel.latitude());
			double lon = parseDouble(vessel.longitude());
			if (Double.isNaN(lat) || Double.isNaN(lon)) continue;

			// Fetch geo from get-vessels-by-area (speed available from by-names or area response)
			mockServicesClient.getVesselsByArea(lat, lon, nearbyRadiusKm);

			// Fetch nearby cities/places from get-places (200 km radius)
			List<PlaceDto> places = mockServicesClient.getPlaces(lat, lon, nearbyRadiusKm);
			List<String> placeNames = places.stream()
					.map(PlaceDto::name)
					.filter(n -> n != null && !n.isBlank())
					.distinct()
					.toList();

			allCities.addAll(placeNames);

			String speed = vessel.speed() != null ? vessel.speed() : "";
			shipItems.add(new ShipMobilityItem(vessel.name(), speed, placeNames));
		}

		List<String> uniqueCities = allCities.stream().sorted().toList();
		return new ShipMobilityResponse(uniqueCities, shipItems);
	}

	private static double parseDouble(String s) {
		if (s == null || s.isBlank()) return Double.NaN;
		try {
			return Double.parseDouble(s.trim());
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}
}
