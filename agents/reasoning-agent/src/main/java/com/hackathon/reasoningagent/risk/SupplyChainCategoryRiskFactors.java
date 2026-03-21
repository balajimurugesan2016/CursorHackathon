package com.hackathon.reasoningagent.risk;

import com.hackathon.reasoningagent.dto.news.CategoryAssignmentDto;
import com.hackathon.reasoningagent.dto.news.ClassifiedArticleDto;
import com.hackathon.reasoningagent.web.dto.CategoryRiskFactorDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Derives a per-category supply-chain risk factor (0–1) from the news category score
 * plus pipeline context: shipping-route impact affinity, catalog geography hits,
 * resolved coordinates, and nearby vessel density.
 */
public final class SupplyChainCategoryRiskFactors {

    private SupplyChainCategoryRiskFactors() {
    }

    public static List<CategoryRiskFactorDto> compute(
            ClassifiedArticleDto article,
            int catalogMentionCount,
            int resolvedLocationCount,
            int totalVesselCount
    ) {
        List<CategoryAssignmentDto> cats = article.categories() != null ? article.categories() : List.of();
        if (cats.isEmpty()) {
            return List.of();
        }

        double shippingProb = article.shippingRouteImpact() != null
                ? article.shippingRouteImpact().probability()
                : 0.0;

        double geoNorm = Math.min(1.0, catalogMentionCount / 6.0);
        double resolveNorm = Math.min(1.0, resolvedLocationCount / 5.0);
        double vesselNorm = Math.min(1.0, totalVesselCount / 25.0);

        List<CategoryRiskFactorDto> out = new ArrayList<>(cats.size());
        for (CategoryAssignmentDto c : cats) {
            double maritimeAffinity = maritimeAffinity(c.categoryId());
            double routeBlend = shippingProb * maritimeAffinity;

            // Context layer: how much operational/geo reality amplifies risk for this theme
            double contextScore = 0.38 * routeBlend
                    + 0.22 * geoNorm
                    + 0.22 * resolveNorm
                    + 0.18 * vesselNorm;
            contextScore = Math.min(1.0, contextScore);

            double base = c.score();
            // Blend news evidence with reasoning context (weights sum to 1)
            double risk = 0.58 * base + 0.42 * contextScore;
            risk = Math.min(1.0, Math.max(0.0, risk));
            risk = round3(risk);

            String rationale = buildRationale(base, shippingProb, maritimeAffinity, catalogMentionCount,
                    resolvedLocationCount, totalVesselCount, risk);

            out.add(new CategoryRiskFactorDto(
                    c.categoryId(),
                    c.categoryLabel(),
                    round3(base),
                    risk,
                    rationale
            ));
        }
        return out;
    }

    /**
     * How strongly shipping-route / maritime disruption context should amplify this theme.
     */
    static double maritimeAffinity(String categoryId) {
        if (categoryId == null) {
            return 0.45;
        }
        return switch (categoryId) {
            case "GEOPOLITICAL_UNREST_SECURITY" -> 1.0;
            case "INFRASTRUCTURE_LOGISTICS_DISRUPTIONS" -> 0.95;
            case "ENVIRONMENTAL_NATURAL_DISASTERS" -> 0.75;
            case "TRADE_POLICY_TARIFFS" -> 0.55;
            case "RAW_MATERIAL_RESOURCE_SCARCITY" -> 0.5;
            case "TECHNOLOGY_CYBERSECURITY" -> 0.4;
            case "CORPORATE_RESTRUCTURING" -> 0.4;
            default -> 0.45;
        };
    }

    private static String buildRationale(
            double newsScore,
            double shippingProb,
            double maritimeAffinity,
            int mentions,
            int resolved,
            int vessels,
            double risk
    ) {
        return String.format(
                Locale.ROOT,
                "News weight %.0f%% + context (route impact × theme %.0f%%, %d place hits, %d resolved, ~%d vessels) → composite %.0f%%",
                newsScore * 100,
                shippingProb * maritimeAffinity * 100,
                mentions,
                resolved,
                vessels,
                risk * 100
        );
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
