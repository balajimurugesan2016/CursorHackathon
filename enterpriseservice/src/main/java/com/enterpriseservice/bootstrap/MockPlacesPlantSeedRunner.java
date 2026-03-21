package com.enterpriseservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Runs after JPA startup; loads plants from {@code mock_plants.json} if none exist.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class MockPlacesPlantSeedRunner implements ApplicationRunner {

	private final MockPlacesPlantSeedService seedService;

	@Override
	public void run(ApplicationArguments args) {
		try {
			seedService.seedIfEmpty();
		} catch (Exception e) {
			log.warn("Could not seed plants from mock_plants.json: {}", e.getMessage());
		}
	}
}
