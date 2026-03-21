package com.hackathon.newsagent.service;

import com.hackathon.newsagent.ai.NewsClassificationResult;
import com.hackathon.newsagent.ai.NewsClassifierAiService;
import com.hackathon.newsagent.client.LocationServiceClient;
import com.hackathon.newsagent.client.MockServicesClient;
import com.hackathon.newsagent.dto.ArticleDto;
import com.hackathon.newsagent.dto.ArticlesResponseDto;
import com.hackathon.newsagent.dto.ClassifiedArticleDto;
import com.hackathon.newsagent.dto.ClassifiedNewsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

@Service
public class NewsClassificationService {

	private static final Logger log = LoggerFactory.getLogger(NewsClassificationService.class);

	private static final Pattern DISPATCH_SUFFIX = Pattern.compile("\\s*[—\\-]\\s*dispatch\\s+\\d+\\s*$", Pattern.CASE_INSENSITIVE);

	private static final List<String> KNOWN_LOCATIONS = List.of(
			"Singapore", "Rotterdam", "Shanghai", "Hong Kong", "Busan", "Hamburg", "Antwerp",
			"Los Angeles", "Houston", "Dubai", "Mumbai", "Strait of Malacca", "Suez Canal",
			"Panama Canal", "Strait of Hormuz", "Jebel Ali", "Piraeus", "Vancouver", "Santos",
			"Seattle", "Taiwan Strait", "Geneva", "Egypt", "Oman", "United Arab Emirates"
	);

	private final MockServicesClient mockServicesClient;
	private final LocationServiceClient locationServiceClient;
	private final NewsClassifierAiService newsClassifierAiService;
	private final ExecutorService classificationExecutor;

	public NewsClassificationService(
			MockServicesClient mockServicesClient,
			LocationServiceClient locationServiceClient,
			NewsClassifierAiService newsClassifierAiService) {
		this.mockServicesClient = mockServicesClient;
		this.locationServiceClient = locationServiceClient;
		this.newsClassifierAiService = newsClassifierAiService;
		this.classificationExecutor = Executors.newFixedThreadPool(10);
	}

	public ClassifiedNewsResponse getClassifiedNews() {
		List<String> cities = locationServiceClient.getCities()
				.onErrorReturn(List.of())
				.block();

		log.info("Location-service output: count={}, cities={}", cities != null ? cities.size() : 0, cities);

		ArticlesResponseDto articlesResponse = mockServicesClient.getArticles()
				.block();

		if (articlesResponse == null || articlesResponse.articles() == null
				|| articlesResponse.articles().results() == null) {
			return new ClassifiedNewsResponse(0, List.of());
		}

		List<ArticleDto> articles = articlesResponse.articles().results();
		String citiesStr = cities != null ? String.join(", ", cities) : "";
		if (cities == null || cities.isEmpty()) {
			log.info("Location-service returned no cities; AI will extract location from article body only");
		}

		List<CompletableFuture<ClassifiedArticleDto>> futures = articles.stream()
				.map(article -> CompletableFuture.supplyAsync(
						() -> classifyArticle(article, citiesStr),
						classificationExecutor))
				.toList();

		List<ClassifiedArticleDto> classified = futures.stream()
				.map(CompletableFuture::join)
				.toList();

		return new ClassifiedNewsResponse(classified.size(), classified);
	}

	private ClassifiedArticleDto classifyArticle(ArticleDto article, String citiesStr) {
		try {
			NewsClassificationResult result = newsClassifierAiService.classify(
					article.title(),
					article.body() != null ? article.body() : "",
					citiesStr);

			log.info("AI output uri={}: title={}, locations={}, topics={}", article.uri(),
					result.title(), result.locations(), result.topics());

			List<String> locations = result.locations();
			if (locations == null || locations.isEmpty()) {
				locations = extractLocationsFromBody(article.body());
				log.debug("AI returned empty locations for uri={}, fallback extracted: {}", article.uri(), locations);
			}

			String title = result.title() != null ? result.title() : article.title();
			return new ClassifiedArticleDto(
					article.uri(),
					stripDispatchSuffix(title),
					locations,
					result.topics() != null ? result.topics() : List.of());
		} catch (Exception e) {
			log.warn("Classification failed for uri={}: {} - using body fallback", article.uri(), e.getMessage());
			List<String> fallbackLocations = extractLocationsFromBody(article.body());
			return new ClassifiedArticleDto(
					article.uri(),
					stripDispatchSuffix(article.title()),
					fallbackLocations,
					List.of());
		}
	}

	private String stripDispatchSuffix(String title) {
		if (title == null || title.isBlank()) return title;
		return DISPATCH_SUFFIX.matcher(title).replaceAll("").trim();
	}

	private List<String> extractLocationsFromBody(String body) {
		if (body == null || body.isBlank()) return List.of("Unknown");
		List<String> found = new ArrayList<>();
		for (String loc : KNOWN_LOCATIONS) {
			if (body.contains(loc) && !found.contains(loc)) {
				found.add(loc);
			}
		}
		return found.isEmpty() ? List.of("Unknown") : found;
	}
}
