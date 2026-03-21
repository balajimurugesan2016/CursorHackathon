package com.enterpriseservice.web;

import com.enterpriseservice.domain.Plant;
import com.enterpriseservice.service.PlantCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Read-only plants API backed by mock_plants.json, same pattern as mockServices
 * (PlaceCatalogController, VesselController, etc.). No database.
 */
@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantController {

	private final PlantCatalogService plantCatalogService;

	@GetMapping
	public List<Plant> list() {
		return plantCatalogService.listAll();
	}

	@GetMapping("/{id}")
	public Plant get(@PathVariable Long id) {
		return plantCatalogService.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
	}
}
