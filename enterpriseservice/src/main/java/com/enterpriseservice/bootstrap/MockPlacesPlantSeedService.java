package com.enterpriseservice.bootstrap;

import com.enterpriseservice.domain.Plant;
import com.enterpriseservice.repository.PlantRepo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds {@link Plant} rows from {@code classpath:mock_plants.json} when the plant table is empty.
 * Mock plant data uses city locations (no chokepoints), mostly in Europe.
 */
@Service
@RequiredArgsConstructor
public class MockPlacesPlantSeedService {

	private final PlantRepo plantRepo;
	private final JsonMapper jsonMapper;

	@Value("classpath:mock_plants.json")
	private Resource mockPlantsResource;

	@Transactional
	public void seedIfEmpty() throws IOException {
		if (plantRepo.count() > 0) {
			return;
		}
		if (!mockPlantsResource.exists()) {
			return;
		}
		List<MockPlantJson> plants = jsonMapper.readValue(
				mockPlantsResource.getInputStream(),
				new TypeReference<List<MockPlantJson>>() {
				});
		List<Plant> batch = new ArrayList<>();
		for (MockPlantJson p : plants) {
			if (p.plantName() == null || p.plantName().isBlank()) {
				continue;
			}
			batch.add(Plant.builder()
					.plantName(p.plantName())
					.location(p.location())
					.latitude(p.latitude())
					.longitude(p.longitude())
					.status(p.status() != null ? p.status() : "ACTIVE")
					.capacityPct(p.capacityPct())
					.totalLines(p.totalLines())
					.linesActive(p.linesActive())
					.build());
		}
		plantRepo.saveAll(batch);
	}
}
