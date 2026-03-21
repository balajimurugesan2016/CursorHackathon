package com.enterpriseservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Runs after {@link MockPlacesPlantSeedRunner} so plants exist before linking suppliers.
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class SupplyChainScenarioSeedRunner implements ApplicationRunner {

	private final SupplyChainScenarioSeedService seedService;

	@Override
	public void run(ApplicationArguments args) {
		try {
			seedService.seedIfEmpty();
		} catch (Exception e) {
			log.warn("Could not seed supply-chain partners: {}", e.getMessage());
		}
	}
}
