package com.hackathon.reasoningagent.service;

import com.hackathon.reasoningagent.client.LocationsAgentClient;
import com.hackathon.reasoningagent.client.NewsAgentClient;
import com.hackathon.reasoningagent.client.PlaceCatalogHttpClient;
import com.hackathon.reasoningagent.client.VesselAgentClient;
import com.hackathon.reasoningagent.dto.catalog.PlaceCatalogEntryDto;
import com.hackathon.reasoningagent.dto.locations.ResolvedLocationDto;
import com.hackathon.reasoningagent.dto.news.AgentRunResponse;
import com.hackathon.reasoningagent.dto.news.ClassifiedArticleDto;
import com.hackathon.reasoningagent.dto.vessel.VesselDto;
import com.hackathon.reasoningagent.dto.vessel.VesselsNearbyDto;
import com.hackathon.reasoningagent.web.dto.ArticleReasoningDto;
import com.hackathon.reasoningagent.web.dto.ArticleReasoningDto.VesselNearLocationDto;
import com.hackathon.reasoningagent.web.dto.ReasoningReportResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class ReasoningPipelineService {

    private final NewsAgentClient newsAgentClient;
    private final PlaceCatalogHttpClient placeCatalogHttpClient;
    private final LocationsAgentClient locationsAgentClient;
    private final VesselAgentClient vesselAgentClient;

    public ReasoningPipelineService(
            NewsAgentClient newsAgentClient,
            PlaceCatalogHttpClient placeCatalogHttpClient,
            LocationsAgentClient locationsAgentClient,
            VesselAgentClient vesselAgentClient
    ) {
        this.newsAgentClient = newsAgentClient;
        this.placeCatalogHttpClient = placeCatalogHttpClient;
        this.locationsAgentClient = locationsAgentClient;
        this.vesselAgentClient = vesselAgentClient;
    }

    public ReasoningReportResponse buildReport(double radiusNm) {
        AgentRunResponse news = newsAgentClient.fetchClassifiedNews();
        List<PlaceCatalogEntryDto> catalog = placeCatalogHttpClient.fetchCatalog();
        catalog = new ArrayList<>(catalog);
        catalog.sort(Comparator.comparingInt((PlaceCatalogEntryDto p) -> p.name() != null ? -p.name().length() : 0));

        List<ArticleReasoningDto> out = new ArrayList<>();
        for (ClassifiedArticleDto article : news.articles()) {
            String text = buildSearchText(article);
            String lower = text.toLowerCase(Locale.ROOT);

            LinkedHashSet<String> mentions = new LinkedHashSet<>();
            for (PlaceCatalogEntryDto place : catalog) {
                if (place.name() == null || place.name().isBlank()) {
                    continue;
                }
                if (lower.contains(place.name().toLowerCase(Locale.ROOT))) {
                    mentions.add(place.name());
                }
            }

            List<ResolvedLocationDto> resolved = new ArrayList<>();
            List<VesselNearLocationDto> vesselBlocks = new ArrayList<>();
            Set<String> vesselSearchKeys = new LinkedHashSet<>();

            double radius = radiusNm;

            for (String placeName : mentions) {
                Optional<ResolvedLocationDto> loc = locationsAgentClient.resolveLocationName(placeName);
                if (loc.isEmpty()) {
                    continue;
                }
                ResolvedLocationDto r = loc.get();
                resolved.add(r);

                String key = String.format(Locale.ROOT, "%.5f:%.5f", r.latitude(), r.longitude());
                if (vesselSearchKeys.contains(key)) {
                    continue;
                }
                vesselSearchKeys.add(key);

                VesselsNearbyDto nearby = vesselAgentClient.fetchVesselsNearby(r.latitude(), r.longitude(), radius);
                List<VesselDto> vessels = nearby.vessels() != null ? nearby.vessels() : List.of();
                vesselBlocks.add(new VesselNearLocationDto(
                        r.matchedName(),
                        r.latitude(),
                        r.longitude(),
                        radius,
                        vessels.size(),
                        vessels
                ));
            }

            out.add(new ArticleReasoningDto(article, List.copyOf(mentions), resolved, vesselBlocks));
        }

        return new ReasoningReportResponse(news.articleCount(), out, radiusNm);
    }

    private static String buildSearchText(ClassifiedArticleDto article) {
        String title = article.title() != null ? article.title() : "";
        String body = article.body() != null ? article.body() : "";
        return title + "\n" + body;
    }
}
