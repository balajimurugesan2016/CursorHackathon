package com.hackathon.newsagent.classification;

import com.hackathon.newsagent.web.dto.ShippingRouteImpactDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * Lexicon-based score for whether an article plausibly affects commercial shipping routes.
 * Raw evidence is mapped to a pseudo-probability in (0,1) via a saturating transform.
 */
@Component
public class ShippingRouteImpactScorer {

    /** Larger = probability rises more slowly with raw evidence. */
    private static final double SATURATION_SCALE = 11.0;

    private static final List<WeightedTerm> TERMS = List.of(
            new WeightedTerm("shipping route", 2.6),
            new WeightedTerm("shipping lane", 2.6),
            new WeightedTerm("sea lane", 2.5),
            new WeightedTerm("maritime route", 2.5),
            new WeightedTerm("trade lane", 2.2),
            new WeightedTerm("chokepoint", 2.5),
            new WeightedTerm("canal closure", 2.7),
            new WeightedTerm("canal transit", 2.0),
            new WeightedTerm("lock closure", 2.0),
            new WeightedTerm("suez", 2.5),
            new WeightedTerm("panama canal", 2.5),
            new WeightedTerm("strait of hormuz", 2.4),
            new WeightedTerm("hormuz", 2.0),
            new WeightedTerm("bab el-mandeb", 2.4),
            new WeightedTerm("bab el mandeb", 2.4),
            new WeightedTerm("malacca strait", 2.3),
            new WeightedTerm("strait of malacca", 2.3),
            new WeightedTerm("red sea", 2.1),
            new WeightedTerm("cape of good hope", 2.3),
            new WeightedTerm("cape route", 2.1),
            new WeightedTerm("around the cape", 2.0),
            new WeightedTerm("northern sea route", 2.0),
            new WeightedTerm("arctic shipping", 1.9),
            new WeightedTerm("trans-pacific", 2.0),
            new WeightedTerm("transatlantic", 1.9),
            new WeightedTerm("trans-atlantic", 1.9),
            new WeightedTerm("reroute", 2.3),
            new WeightedTerm("rerouting", 2.3),
            new WeightedTerm("diversion", 2.0),
            new WeightedTerm("vessel diversion", 2.4),
            new WeightedTerm("ship diversion", 2.4),
            new WeightedTerm("alternate route", 2.1),
            new WeightedTerm("alternative route", 2.1),
            new WeightedTerm("detour", 1.8),
            new WeightedTerm("shipping lane closure", 2.7),
            new WeightedTerm("lane closure", 1.6),
            new WeightedTerm("war risk premium", 2.4),
            new WeightedTerm("war risk", 1.9),
            new WeightedTerm("war surcharge", 2.2),
            new WeightedTerm("piracy", 2.2),
            new WeightedTerm("maritime security", 1.8),
            new WeightedTerm("houthi", 2.0),
            new WeightedTerm("port closure", 2.2),
            new WeightedTerm("port congestion", 1.9),
            new WeightedTerm("draft restriction", 1.9),
            new WeightedTerm("low water", 1.7),
            new WeightedTerm("vessel queue", 1.9),
            new WeightedTerm("tanker queue", 2.0),
            new WeightedTerm("shipping backlog", 1.8),
            new WeightedTerm("blank sailing", 2.0),
            new WeightedTerm("skipped port", 1.9),
            new WeightedTerm("vessel delay", 1.7),
            new WeightedTerm("schedule reliability", 1.4),
            new WeightedTerm("container line", 1.5),
            new WeightedTerm("ocean carrier", 1.6),
            new WeightedTerm("strait", 1.2),
            new WeightedTerm("canal", 1.0),
            new WeightedTerm("tanker", 1.4),
            new WeightedTerm("bulk carrier", 1.5),
            new WeightedTerm("container ship", 1.5),
            new WeightedTerm("freight rate", 1.0),
            new WeightedTerm("spot freight", 1.2),
            new WeightedTerm("baltic dry index", 1.3),
            new WeightedTerm("insurance premium", 1.2),
            new WeightedTerm("marine insurance", 1.4)
    );

    public ShippingRouteImpactDto score(String title, String body) {
        String text = ((title != null ? title : "") + "\n" + (body != null ? body : ""))
                .toLowerCase(Locale.ROOT);

        LinkedHashSet<String> matched = new LinkedHashSet<>();
        double raw = 0.0;
        for (WeightedTerm t : TERMS) {
            if (text.contains(t.term())) {
                raw += t.weight();
                matched.add(t.term());
            }
        }

        double probability = rawToProbability(raw);
        return new ShippingRouteImpactDto(
                round4(probability),
                List.copyOf(matched)
        );
    }

    /**
     * Maps non-negative evidence to (0,1); 0 raw → 0; grows toward 1 with diminishing returns.
     */
    static double rawToProbability(double raw) {
        if (raw <= 0.0) {
            return 0.0;
        }
        return 1.0 - Math.exp(-raw / SATURATION_SCALE);
    }

    private static double round4(double v) {
        return Math.round(v * 10000.0) / 10000.0;
    }

    private record WeightedTerm(String term, double weight) {
    }
}
