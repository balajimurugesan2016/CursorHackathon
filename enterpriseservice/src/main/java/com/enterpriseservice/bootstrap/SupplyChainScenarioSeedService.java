package com.enterpriseservice.bootstrap;

import com.enterpriseservice.domain.Plant;
import com.enterpriseservice.domain.Supplier;
import com.enterpriseservice.repository.PlantRepo;
import com.enterpriseservice.repository.SupplierRepo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Seeds demo suppliers and plant–supplier links aligned with chokepoint / Gulf geography from
 * {@code mock_places.json}, so the supply-chain risk agent can correlate news + vessels with enterprise sites.
 */
@Service
@RequiredArgsConstructor
public class SupplyChainScenarioSeedService {

	private final PlantRepo plantRepo;
	private final SupplierRepo supplierRepo;
	private final ObjectMapper objectMapper;

	@Value("classpath:mock_supply_partners.json")
	private Resource partnersResource;

	@Transactional
	public void seedIfEmpty() throws IOException {
		if (supplierRepo.count() > 0) {
			return;
		}
		if (!partnersResource.exists()) {
			return;
		}
		List<MockSupplyPartnerJson> rows = objectMapper.readValue(
				partnersResource.getInputStream(),
				new TypeReference<List<MockSupplyPartnerJson>>() {
				});
		List<Supplier> batch = new ArrayList<>();
		for (MockSupplyPartnerJson r : rows) {
			if (r.supplierName() == null || r.supplierName().isBlank()) {
				continue;
			}
			batch.add(Supplier.builder()
					.supplierName(r.supplierName())
					.location(r.location())
					.latitude(r.latitude())
					.longitude(r.longitude())
					.contractStatus(r.contractStatus() != null ? r.contractStatus() : "ACTIVE")
					.build());
		}
		supplierRepo.saveAll(batch);

		List<Plant> plants = plantRepo.findAll();
		Map<String, Supplier> byName = supplierRepo.findAll().stream()
				.collect(java.util.stream.Collectors.toMap(Supplier::getSupplierName, s -> s, (a, b) -> a));

		Supplier gulf = byName.get("Gulf Petrochem Supplies LLC");
		Supplier hormuz = byName.get("Strait Logistics Cooperative");
		Supplier malacca = byName.get("Malacca Strait Feeder JV");

		link(plants, "Dubai City", gulf);
		link(plants, "Port Jebel Ali", gulf);
		link(plants, "Strait of Hormuz", hormuz);
		link(plants, "Strait of Malacca", malacca);
	}

	private void link(List<Plant> plants, String plantNameExact, Supplier supplier) {
		if (supplier == null) {
			return;
		}
		Optional<Plant> p = plants.stream()
				.filter(x -> Objects.equals(plantNameExact, x.getPlantName()))
				.findFirst();
		if (p.isEmpty()) {
			return;
		}
		Plant plant = p.get();
		if (plant.getSuppliers().stream()
				.anyMatch(sup -> sup.getId() != null && sup.getId().equals(supplier.getId()))) {
			return;
		}
		plant.getSuppliers().add(supplier);
		supplier.getPlants().add(plant);
		plantRepo.save(plant);
	}
}
