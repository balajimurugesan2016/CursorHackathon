package com.hackathon.probability.service;

import com.hackathon.probability.config.ProbabilityProperties;
import com.hackathon.probability.dto.ClassifiedArticleDto;
import com.hackathon.probability.dto.ClassifiedNewsResponse;
import com.hackathon.probability.dto.ProbabilityItem;
import com.hackathon.probability.dto.ProbabilityResponse;
import com.hackathon.probability.dto.ShipMobilityItem;
import com.hackathon.probability.dto.ShipMobilityResponse;
import com.hackathon.probability.client.NewsAgentClient;
import com.hackathon.probability.client.ShipMobilityClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProbabilityService {

	private static final Logger log = LoggerFactory.getLogger(ProbabilityService.class);

	private final NewsAgentClient newsAgentClient;
	private final ShipMobilityClient shipMobilityClient;
	private final ProbabilityProperties properties;

	public ProbabilityService(NewsAgentClient newsAgentClient,
			ShipMobilityClient shipMobilityClient,
			ProbabilityProperties properties) {
		this.newsAgentClient = newsAgentClient;
		this.shipMobilityClient = shipMobilityClient;
		this.properties = properties;
	}

	public ProbabilityResponse getProbabilities() {
		ClassifiedNewsResponse newsResponse = newsAgentClient.getClassifiedNews();
		ShipMobilityResponse mobilityResponse = shipMobilityClient.getShipMobility();

		if (newsResponse == null || newsResponse.articles() == null || newsResponse.articles().isEmpty()) {
			return new ProbabilityResponse(0, List.of());
		}

		List<ShipMobilityItem> ships = mobilityResponse != null && mobilityResponse.ships() != null
				? mobilityResponse.ships()
				: List.of();

		List<ProbabilityItem> items = newsResponse.articles().stream()
				.map(article -> computeProbability(article, ships))
				.toList();

		return new ProbabilityResponse(items.size(), items);
	}

	private ProbabilityItem computeProbability(ClassifiedArticleDto article, List<ShipMobilityItem> ships) {
		List<String> locations = article.locations() != null ? article.locations() : List.of();
		double baseScore = 0;

		for (ShipMobilityItem ship : ships) {
			double speedKn = parseSpeed(ship.speed());
			boolean locationMatch = hasLocationMatch(locations, ship.cities());

			if (locationMatch) {
				baseScore += properties.weightLocationMatch();
			}
			if (speedKn < properties.speedThresholdKn()) {
				baseScore += properties.weightSpeedLow();
				if (locationMatch) {
					baseScore += properties.weightLocationAndSpeed();
				}
			}
		}

		int probabilityPercent = normalizeToPercent(baseScore);

		// Gulf region articles stay at high risk (minimum floor)
		if (isGulfArticle(locations)) {
			int gulfMin = properties.gulfMinPercent();
			probabilityPercent = Math.max(probabilityPercent, gulfMin);
		}

		log.debug("Article '{}' locations={} score={} -> {}%", article.title(), locations, baseScore, probabilityPercent);

		return new ProbabilityItem(
				article.title() != null ? article.title() : "",
				locations,
				probabilityPercent);
	}

	private boolean isGulfArticle(List<String> locations) {
		String gulfList = properties.gulfLocations();
		if (gulfList == null || gulfList.isBlank() || locations == null || locations.isEmpty()) {
			return false;
		}
		Set<String> gulfTerms = java.util.Arrays.stream(gulfList.split(","))
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		for (String loc : locations) {
			if (loc == null || loc.isBlank()) continue;
			String normalized = loc.trim().toLowerCase();
			if (gulfTerms.contains(normalized)) return true;
			for (String gulf : gulfTerms) {
				if (normalized.contains(gulf) || gulf.contains(normalized)) return true;
			}
		}
		return false;
	}

	private static double parseSpeed(String speedStr) {
		if (speedStr == null || speedStr.isBlank()) {
			return Double.MAX_VALUE;
		}
		try {
			int raw = Integer.parseInt(speedStr.trim());
			return raw / 10.0;
		} catch (NumberFormatException e) {
			return Double.MAX_VALUE;
		}
	}

	private static boolean hasLocationMatch(List<String> eventLocations, List<String> shipCities) {
		if (eventLocations == null || eventLocations.isEmpty() || shipCities == null || shipCities.isEmpty()) {
			return false;
		}
		Set<String> normalizedCities = shipCities.stream()
				.filter(c -> c != null && !c.isBlank())
				.map(String::trim)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		for (String loc : eventLocations) {
			if (loc == null || loc.isBlank()) continue;
			String normalized = loc.trim().toLowerCase();
			if (normalizedCities.contains(normalized)) {
				return true;
			}
			for (String city : normalizedCities) {
				if (city.contains(normalized) || normalized.contains(city)) {
					return true;
				}
			}
		}
		return false;
	}

	private int normalizeToPercent(double baseScore) {
		int maxScore = properties.maxScore();
		double ratio = Math.min(1.0, baseScore / maxScore);
		return Math.min(100, (int) Math.round(ratio * 100));
	}
}
