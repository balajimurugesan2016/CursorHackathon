package com.hackathon.shipmobility.web;

import com.hackathon.shipmobility.dto.ShipMobilityResponse;
import com.hackathon.shipmobility.service.ShipMobilityService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ship-mobility")
public class ShipMobilityController {

	private final ShipMobilityService shipMobilityService;

	public ShipMobilityController(ShipMobilityService shipMobilityService) {
		this.shipMobilityService = shipMobilityService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ShipMobilityResponse getShipMobility() {
		return shipMobilityService.getShipMobility();
	}
}
