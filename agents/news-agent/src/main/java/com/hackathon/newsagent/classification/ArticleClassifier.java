package com.hackathon.newsagent.classification;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Lexicon-based multi-label classifier. Tune {@link #RULES} for precision/recall.
 */
@Component
public class ArticleClassifier {

    private static final List<WeightedTerm> RULES = List.of(
            // Geopolitical Unrest & Security
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "red sea", 2.2),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "gulf of oman", 2.0),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "strait", 1.0),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "iran", 1.8),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "qatar", 1.6),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "sanction", 1.7),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "embargo", 1.8),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "tanker", 1.6),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "naval", 1.4),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "maritime security", 2.0),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "drone", 1.5),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "military", 1.5),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "diplomatic", 1.0),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "geopolitical", 1.8),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "war surcharge", 2.5),
            new WeightedTerm(SupplyChainCategory.GEOPOLITICAL_UNREST_SECURITY, "insurance premium", 1.2),

            // Trade Policy and Tariffs
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "tariff", 2.2),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "trade war", 2.4),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "import duty", 1.8),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "customs", 1.2),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "trade compliance", 1.9),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "anti-dumping", 1.8),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "wto", 1.0),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "import ban", 1.7),
            new WeightedTerm(SupplyChainCategory.TRADE_POLICY_TARIFFS, "export control", 1.8),

            // Environmental & Natural Disasters
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "earthquake", 2.2),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "hurricane", 2.2),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "typhoon", 2.0),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "cyclone", 1.8),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "flood", 1.6),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "wildfire", 1.6),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "severe weather", 1.5),
            new WeightedTerm(SupplyChainCategory.ENVIRONMENTAL_NATURAL_DISASTERS, "tsunami", 1.8),

            // Infrastructure & Logistics Disruptions
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "port congestion", 2.2),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "container shortage", 2.0),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "shipping backlog", 1.8),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "strike", 1.6),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "lockout", 1.5),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "bottleneck", 1.4),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "logistics hub", 1.5),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "freight", 0.9),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "trucking", 1.2),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "rail yard", 1.3),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "lock operations", 1.6),
            new WeightedTerm(SupplyChainCategory.INFRASTRUCTURE_LOGISTICS_DISRUPTIONS, "transit", 0.7),

            // Raw Material & Resource Scarcity
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "semiconductor", 2.0),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "chip shortage", 2.0),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "helium", 1.6),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "rare earth", 1.8),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "raw material", 1.5),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "material scarcity", 1.9),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "battery component", 1.4),
            new WeightedTerm(SupplyChainCategory.RAW_MATERIAL_RESOURCE_SCARCITY, "steel coil", 1.0),

            // Technology & Cybersecurity
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "ransomware", 2.3),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "cybersecurity", 1.8),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "cyber attack", 2.0),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "cyber incident", 1.9),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "breach", 1.4),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "malware", 1.6),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "system failure", 1.5),
            new WeightedTerm(SupplyChainCategory.TECHNOLOGY_CYBERSECURITY, "outage", 1.2),

            // Corporate Restructuring
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "layoff", 1.9),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "restructuring", 1.7),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "facility closure", 2.0),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "plant closure", 2.0),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "store closure", 1.6),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "downsizing", 1.5),
            new WeightedTerm(SupplyChainCategory.CORPORATE_RESTRUCTURING, "retailer", 0.8)
    );

    public List<CategoryScore> classify(String title, String body) {
        String text = ((title != null ? title : "") + "\n" + (body != null ? body : ""))
                .toLowerCase(Locale.ROOT);

        Map<SupplyChainCategory, Double> raw = new EnumMap<>(SupplyChainCategory.class);
        Map<SupplyChainCategory, Set<String>> signals = new EnumMap<>(SupplyChainCategory.class);

        for (WeightedTerm rule : RULES) {
            if (text.contains(rule.term())) {
                raw.merge(rule.category(), rule.weight(), Double::sum);
                signals.computeIfAbsent(rule.category(), k -> new LinkedHashSet<>()).add(rule.term());
            }
        }

        double total = raw.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0) {
            return List.of();
        }

        List<CategoryScore> list = new ArrayList<>();
        for (Map.Entry<SupplyChainCategory, Double> e : raw.entrySet()) {
            double share = e.getValue() / total;
            list.add(new CategoryScore(
                    e.getKey(),
                    round3(share),
                    List.copyOf(signals.getOrDefault(e.getKey(), Set.of()))
            ));
        }
        list.sort(Comparator.comparingDouble(CategoryScore::score).reversed());
        return list;
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }

    private record WeightedTerm(SupplyChainCategory category, String term, double weight) {
    }
}
