package com.enterpriseservice.service;

import com.enterpriseservice.bootstrap.MockPlantJson;
import com.enterpriseservice.domain.Plant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Read-only catalog of plants from mock_plants.json. Serves data the same way mockServices
 * serves mock_places, mock_vessels, etc. — directly from JSON, no database.
 */
@Service
@RequiredArgsConstructor
public class PlantCatalogService {

	@Value("classpath:mock_plants.json")
	private Resource mockPlantsResource;

	private final JsonMapper jsonMapper;

	private List<Plant> plants = new ArrayList<>();

	@PostConstruct
	public void loadCatalog() throws IOException {
		if (!mockPlantsResource.exists()) {
			throw new IllegalStateException("mock_plants.json not found in classpath");
		}
		List<MockPlantJson> raw = jsonMapper.readValue(
				mockPlantsResource.getInputStream(),
				new TypeReference<List<MockPlantJson>>() {});
		plants = new ArrayList<>();
		for (int i = 0; i < raw.size(); i++) {
			MockPlantJson p = raw.get(i);
			if (p.plantName() == null || p.plantName().isBlank()) continue;
			Plant plant = Plant.builder()
					.id((long) (i + 1))
					.plantName(p.plantName())
					.location(p.location())
					.latitude(p.latitude())
					.longitude(p.longitude())
					.status(p.status() != null ? p.status() : "ACTIVE")
					.capacityPct(p.capacityPct())
					.totalLines(p.totalLines())
					.linesActive(p.linesActive())
					.build();
			plants.add(plant);
		}
	}

	public List<Plant> listAll() {
		return new ArrayList<>(plants);
	}

	public Optional<Plant> getById(Long id) {
		int idx = id != null ? id.intValue() - 1 : -1;
		if (idx >= 0 && idx < plants.size()) {
			return Optional.of(plants.get(idx));
		}
		return Optional.empty();
	}
}
