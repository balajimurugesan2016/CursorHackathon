package com.hackathon.supplychainrisk.service;

import com.hackathon.supplychainrisk.dto.reasoning.ArticleReasoningDto;
import com.hackathon.supplychainrisk.dto.reasoning.VesselDto;

import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

/**
 * Estimates how soon maritime traffic could interact with a fixed point (plant/supplier),
 * and converts that into an imminence score combined with category risk for
 * "disturbance certainty" (confidence that the supply chain is materially disrupted soon).
 */
public final class MaritimeDisturbanceCalculator {

    /** Hours: shorter time → higher imminence. */
    private static final double IMMINENCE_HALF_LIFE_H = 48.0;

    private MaritimeDisturbanceCalculator() {
    }

    /**
     * Minimum time (hours) for any vessel with valid position and speed to reach the entity point
     * (straight-line distance / speed). Returns empty if no usable vessel speeds.
     */
    public static OptionalDouble minHoursToEntity(double entityLat, double entityLon, ArticleReasoningDto art) {
        List<ArticleReasoningDto.VesselNearLocationDto> blocks =
                art.vesselsNearLocations() != null ? art.vesselsNearLocations() : List.of();
        double best = Double.POSITIVE_INFINITY;
        boolean any = false;
        for (ArticleReasoningDto.VesselNearLocationDto b : blocks) {
            List<VesselDto> vessels = b.vessels() != null ? b.vessels() : List.of();
            for (VesselDto v : vessels) {
                OptionalDouble spd = parseSpeedKnots(v.speed());
                OptionalDouble vlat = parseCoord(v.latitude());
                OptionalDouble vlon = parseCoord(v.longitude());
                if (spd.isEmpty() || vlat.isEmpty() || vlon.isEmpty()) {
                    continue;
                }
                double km = Geo.haversineKm(vlat.getAsDouble(), vlon.getAsDouble(), entityLat, entityLon);
                double kmh = spd.getAsDouble() * 1.852;
                if (kmh <= 0.01) {
                    continue;
                }
                double hours = km / kmh;
                if (hours >= 0 && hours < best) {
                    best = hours;
                    any = true;
                }
            }
        }
        if (!any || !Double.isFinite(best)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(best);
    }

    /**
     * Imminence 0–1: higher when vessels are close in time (or present without ETA).
     */
    public static double imminence(OptionalDouble minHours, ArticleReasoningDto art) {
        if (minHours.isPresent()) {
            double h = minHours.getAsDouble();
            return 1.0 / (1.0 + h / IMMINENCE_HALF_LIFE_H);
        }
        int vesselRows = countVesselRows(art);
        if (vesselRows > 0) {
            return 0.38;
        }
        return 0.08;
    }

    /**
     * Certainty that the supply chain is disturbed: blends category risk with maritime imminence.
     * When imminence is low, risk still contributes ~55% of its value (geographic/text exposure alone).
     */
    public static double disturbanceCertainty(double categoryRisk, OptionalDouble minHours, ArticleReasoningDto art) {
        double imm = imminence(minHours, art);
        return Math.min(1.0, categoryRisk * (0.55 + 0.45 * imm));
    }

    public static int countVesselRows(ArticleReasoningDto art) {
        List<ArticleReasoningDto.VesselNearLocationDto> blocks =
                art.vesselsNearLocations() != null ? art.vesselsNearLocations() : List.of();
        int n = 0;
        for (ArticleReasoningDto.VesselNearLocationDto b : blocks) {
            if (b.vessels() != null) {
                n += b.vessels().size();
            }
        }
        return n;
    }

    /**
     * Mock data sometimes encodes speed in tenths of knots (e.g. 110 → 11 kn).
     */
    static OptionalDouble parseSpeedKnots(String speed) {
        if (speed == null || speed.isBlank()) {
            return OptionalDouble.empty();
        }
        try {
            double v = Double.parseDouble(speed.trim());
            if (v > 55.0) {
                v = v / 10.0;
            }
            v = Math.min(45.0, Math.max(0.1, v));
            return OptionalDouble.of(v);
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    static OptionalDouble parseCoord(String s) {
        if (s == null || s.isBlank()) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(s.trim()));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    public static String formatHours(Double hours) {
        if (hours == null || !Double.isFinite(hours)) {
            return "—";
        }
        if (hours < 72) {
            return String.format(Locale.US, "~%.1f h", hours);
        }
        return String.format(Locale.US, "~%.1f d", hours / 24.0);
    }
}
