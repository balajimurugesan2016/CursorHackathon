package com.hackathon.supplychainrisk.service;

import com.hackathon.supplychainrisk.dto.PlantSupplyRiskDto;
import com.hackathon.supplychainrisk.dto.SupplierSupplyRiskDto;
import com.hackathon.supplychainrisk.dto.SupplyChainRiskReportResponse;
import com.hackathon.supplychainrisk.dto.enterprise.EnterprisePlantDto;
import com.hackathon.supplychainrisk.dto.enterprise.EnterpriseSupplierDto;
import com.hackathon.supplychainrisk.dto.reasoning.ArticleReasoningDto;
import com.hackathon.supplychainrisk.dto.reasoning.CategoryRiskFactorDto;
import com.hackathon.supplychainrisk.dto.reasoning.ReasoningReportResponse;
import com.hackathon.supplychainrisk.dto.reasoning.ResolvedLocationDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class SupplyChainRiskAnalyzer {

    public SupplyChainRiskReportResponse analyze(
            List<EnterprisePlantDto> plants,
            ReasoningReportResponse reasoning,
            double proximityRadiusKm
    ) {
        List<ArticleReasoningDto> articles = reasoning.articles() != null ? reasoning.articles() : List.of();
        if (plants == null || plants.isEmpty()) {
            return new SupplyChainRiskReportResponse(
                    0.0,
                    "No plants in enterprise service — seed or create plants to assess supply-chain exposure.",
                    reasoning.articleCount(),
                    reasoning.searchRadiusNm(),
                    0,
                    List.of()
            );
        }

        List<PlantSupplyRiskDto> out = new ArrayList<>();
        double portfolioMax = 0.0;

        for (EnterprisePlantDto plant : plants) {
            PlantSupplyRiskDto pr = analyzePlant(plant, articles, proximityRadiusKm);
            out.add(pr);
            portfolioMax = Math.max(portfolioMax, pr.plantRiskScore());
        }

        out.sort(Comparator.comparingDouble(PlantSupplyRiskDto::plantRiskScore).reversed());

        String rationale = portfolioRationale(portfolioMax, plants.size(), reasoning.articleCount(), articles.size());

        return new SupplyChainRiskReportResponse(
                round3(portfolioMax),
                rationale,
                reasoning.articleCount(),
                reasoning.searchRadiusNm(),
                plants.size(),
                out
        );
    }

    private static String portfolioRationale(
            double portfolioMax,
            int plantCount,
            int reasoningArticleCount,
            int articleListSize
    ) {
        if (reasoningArticleCount == 0 || articleListSize == 0) {
            return "No reasoning articles with risk signals — ensure news-agent, locations-agent, and vessel-agent are up.";
        }
        return String.format(
                Locale.US,
                "Portfolio risk %.3f (max across %d plants), from %d reasoning article(s).",
                portfolioMax,
                plantCount,
                reasoningArticleCount
        );
    }

    private PlantSupplyRiskDto analyzePlant(
            EnterprisePlantDto plant,
            List<ArticleReasoningDto> articles,
            double proximityRadiusKm
    ) {
        List<EnterpriseSupplierDto> sups = plant.suppliers() != null ? plant.suppliers() : List.of();
        Map<Long, SupplierAccum> byId = new LinkedHashMap<>();
        for (EnterpriseSupplierDto s : sups) {
            if (s.id() != null) {
                byId.putIfAbsent(s.id(), new SupplierAccum());
            }
        }

        double plantDirect = 0.0;
        LinkedHashSet<String> plantSignals = new LinkedHashSet<>();

        for (ArticleReasoningDto art : articles) {
            double ar = maxCategoryRisk(art);
            if (ar <= 0.0) {
                continue;
            }
            String title = articleTitle(art);

            Exposure plantExp = exposureForEntity(
                    plant.plantName(),
                    plant.location(),
                    plant.latitude(),
                    plant.longitude(),
                    art,
                    proximityRadiusKm
            );
            if (plantExp.exposed()) {
                plantDirect = Math.max(plantDirect, ar);
                plantSignals.add(plantExp.signal() + " — \"" + title + "\"");
            }

            for (EnterpriseSupplierDto s : sups) {
                if (s.id() == null) {
                    continue;
                }
                Exposure se = exposureForEntity(
                        s.supplierName(),
                        s.location(),
                        s.latitude(),
                        s.longitude(),
                        art,
                        proximityRadiusKm
                );
                if (se.exposed()) {
                    SupplierAccum acc = byId.computeIfAbsent(s.id(), k -> new SupplierAccum());
                    acc.add(ar, title, se.signal());
                }
            }
        }

        double supplierMax = byId.values().stream().mapToDouble(SupplierAccum::maxRisk).max().orElse(0.0);
        double plantScore = Math.max(plantDirect, supplierMax);

        List<SupplierSupplyRiskDto> supplierRows = new ArrayList<>();
        for (EnterpriseSupplierDto s : sups) {
            SupplierAccum acc = s.id() != null ? byId.get(s.id()) : null;
            double score = acc != null ? acc.maxRisk : 0.0;
            supplierRows.add(new SupplierSupplyRiskDto(
                    s.id(),
                    s.supplierName() != null ? s.supplierName() : "?",
                    round3(score),
                    acc != null ? List.copyOf(acc.titles) : List.of(),
                    acc != null ? List.copyOf(acc.signals) : List.of()
            ));
        }

        supplierRows.sort(Comparator.comparingDouble(SupplierSupplyRiskDto::riskScore).reversed());

        String plantRationale = buildPlantRationale(plant.plantName(), plantScore, plantSignals, plantDirect, supplierMax);

        return new PlantSupplyRiskDto(
                plant.id(),
                plant.plantName() != null ? plant.plantName() : "?",
                plant.location(),
                round3(plantScore),
                plantRationale,
                supplierRows
        );
    }

    private static String buildPlantRationale(
            String plantName,
            double plantScore,
            LinkedHashSet<String> plantSignals,
            double plantDirect,
            double supplierMax
    ) {
        if (plantScore <= 0.0) {
            return "No geographic or text overlap with current reasoning locations and catalog mentions.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, "Max exposure risk %.3f", plantScore));
        if (plantDirect > 0 && supplierMax > 0) {
            sb.append(" (plant and supplier signals).");
        } else if (plantDirect > 0) {
            sb.append(" (plant site matches news geography).");
        } else {
            sb.append(" (via linked suppliers).");
        }
        if (!plantSignals.isEmpty()) {
            sb.append(" Examples: ");
            int i = 0;
            for (String p : plantSignals) {
                if (i++ >= 2) {
                    break;
                }
                sb.append(p);
                if (i < Math.min(2, plantSignals.size())) {
                    sb.append("; ");
                }
            }
        }
        return sb.toString();
    }

    private record Exposure(boolean exposed, String signal) {
    }

    private Exposure exposureForEntity(
            String name,
            String locationLabel,
            String latStr,
            String lonStr,
            ArticleReasoningDto art,
            double proximityRadiusKm
    ) {
        Optional<Coord> coord = parseCoord(latStr, lonStr);
        List<ResolvedLocationDto> locs = art.resolvedLocations() != null ? art.resolvedLocations() : List.of();
        if (coord.isPresent()) {
            Coord c = coord.get();
            for (ResolvedLocationDto r : locs) {
                double km = Geo.haversineKm(c.lat, c.lon, r.latitude(), r.longitude());
                if (km <= proximityRadiusKm) {
                    String label = r.matchedName() != null ? r.matchedName() : r.query();
                    return new Exposure(
                            true,
                            String.format(
                                    Locale.US,
                                    "%.0fkm from news location \"%s\"",
                                    km,
                                    label != null ? label : "?"
                            )
                    );
                }
            }
        }
        if (textOverlap(name, locationLabel, art)) {
            return new Exposure(true, "Name/location overlap with catalog mention or resolved place text");
        }
        return new Exposure(false, "");
    }

    private static boolean textOverlap(String name, String locationLabel, ArticleReasoningDto art) {
        List<String> mentions = art.catalogMentions() != null ? art.catalogMentions() : List.of();
        for (String m : mentions) {
            if (m == null || m.isBlank()) {
                continue;
            }
            if (containsInsensitive(name, m) || containsInsensitive(locationLabel, m)) {
                return true;
            }
        }
        List<ResolvedLocationDto> locs = art.resolvedLocations() != null ? art.resolvedLocations() : List.of();
        for (ResolvedLocationDto r : locs) {
            if (r.matchedName() != null && (containsInsensitive(name, r.matchedName())
                    || containsInsensitive(locationLabel, r.matchedName()))) {
                return true;
            }
            if (r.query() != null && (containsInsensitive(name, r.query())
                    || containsInsensitive(locationLabel, r.query()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsInsensitive(String hay, String needle) {
        if (hay == null || needle == null || needle.isBlank()) {
            return false;
        }
        return hay.toLowerCase(Locale.ROOT).contains(needle.trim().toLowerCase(Locale.ROOT));
    }

    private record Coord(double lat, double lon) {
    }

    private static Optional<Coord> parseCoord(String latStr, String lonStr) {
        if (latStr == null || lonStr == null || latStr.isBlank() || lonStr.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new Coord(
                    Double.parseDouble(latStr.trim()),
                    Double.parseDouble(lonStr.trim())
            ));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static double maxCategoryRisk(ArticleReasoningDto a) {
        List<CategoryRiskFactorDto> cats = a.categoryRisks();
        if (cats == null || cats.isEmpty()) {
            return 0.0;
        }
        return cats.stream().mapToDouble(CategoryRiskFactorDto::riskFactor).max().orElse(0.0);
    }

    private static String articleTitle(ArticleReasoningDto a) {
        if (a.classified() == null || a.classified().title() == null || a.classified().title().isBlank()) {
            return "Article";
        }
        return a.classified().title();
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    private static final class SupplierAccum {
        private double maxRisk;
        private final LinkedHashSet<String> titles = new LinkedHashSet<>();
        private final List<String> signals = new ArrayList<>();

        void add(double risk, String title, String signal) {
            maxRisk = Math.max(maxRisk, risk);
            if (titles.size() < 6) {
                titles.add(title);
            }
            if (signals.size() < 10) {
                signals.add(signal);
            }
        }

        double maxRisk() {
            return maxRisk;
        }

        LinkedHashSet<String> titles() {
            return titles;
        }

        List<String> signals() {
            return signals;
        }
    }
}
