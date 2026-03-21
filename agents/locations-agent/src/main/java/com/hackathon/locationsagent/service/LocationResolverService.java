package com.hackathon.locationsagent.service;

import com.hackathon.locationsagent.config.ResolveProperties;
import com.hackathon.locationsagent.model.PlaceDto;
import com.hackathon.locationsagent.resolve.MatchKind;
import com.hackathon.locationsagent.resolve.ResolvedLocation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class LocationResolverService {

    private final PlaceCatalogService catalogService;
    private final ResolveProperties resolveProperties;

    public LocationResolverService(PlaceCatalogService catalogService, ResolveProperties resolveProperties) {
        this.catalogService = catalogService;
        this.resolveProperties = resolveProperties;
    }

    public List<ResolvedLocation> resolveQueries(List<String> queries) {
        List<PlaceDto> places = catalogService.getOrLoad();
        List<ResolvedLocation> out = new ArrayList<>();
        for (String q : queries) {
            if (q == null || q.isBlank()) {
                continue;
            }
            resolveOne(q, places).ifPresent(out::add);
        }
        return out;
    }

    public Optional<ResolvedLocation> resolveOne(String rawQuery, List<PlaceDto> places) {
        String query = normalize(rawQuery);
        if (query.isEmpty()) {
            return Optional.empty();
        }

        double min = resolveProperties.minConfidence();
        Candidate best = null;

        for (PlaceDto p : places) {
            String name = p.name();
            if (name == null || !hasValidCoords(p)) {
                continue;
            }
            String n = normalize(name);

            if (n.equals(query)) {
                return Optional.of(toResolved(rawQuery, p, MatchKind.EXACT, 1.0));
            }

            double c;
            MatchKind kind;
            if (n.contains(query) || query.contains(n)) {
                c = n.contains(query) ? 0.9 : 0.85;
                kind = MatchKind.CONTAINS;
            } else {
                double token = tokenOverlapScore(query, n);
                double lev = levenshteinRatio(query, n);
                if (token >= lev) {
                    c = token;
                    kind = MatchKind.TOKEN_OVERLAP;
                } else {
                    c = lev;
                    kind = MatchKind.FUZZY;
                }
            }

            if (c < min) {
                continue;
            }
            if (best == null || c > best.score() || (c == best.score() && name.length() < best.place().name().length())) {
                best = new Candidate(p, c, kind);
            }
        }

        if (best == null) {
            return Optional.empty();
        }
        return Optional.of(toResolved(rawQuery, best.place(), best.kind(), round3(best.score())));
    }

    private static boolean hasValidCoords(PlaceDto p) {
        try {
            Double.parseDouble(p.latitude());
            Double.parseDouble(p.longitude());
            return true;
        } catch (NullPointerException | NumberFormatException e) {
            return false;
        }
    }

    private static ResolvedLocation toResolved(String rawQuery, PlaceDto p, MatchKind kind, double confidence) {
        double lat = Double.parseDouble(p.latitude());
        double lon = Double.parseDouble(p.longitude());
        return new ResolvedLocation(
                rawQuery.trim(),
                p.name(),
                p.type(),
                lat,
                lon,
                kind,
                confidence
        );
    }

    private record Candidate(PlaceDto place, double score, MatchKind kind) {
    }

    private static String normalize(String s) {
        String t = s.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        t = t.replaceAll("^the\\s+", "");
        return t;
    }

    private static double tokenOverlapScore(String a, String b) {
        Set<String> ta = tokens(a);
        Set<String> tb = tokens(b);
        if (ta.isEmpty() || tb.isEmpty()) {
            return 0;
        }
        Set<String> inter = new HashSet<>(ta);
        inter.retainAll(tb);
        Set<String> union = new LinkedHashSet<>(ta);
        union.addAll(tb);
        return (double) inter.size() / union.size();
    }

    private static Set<String> tokens(String s) {
        String[] parts = s.split("[^a-z0-9]+");
        Set<String> out = new HashSet<>();
        for (String p : parts) {
            if (!p.isEmpty() && p.length() > 1) {
                out.add(p);
            }
        }
        return out;
    }

    private static double levenshteinRatio(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        int d = levenshtein(a, b);
        int max = Math.max(a.length(), b.length());
        return 1.0 - (double) d / max;
    }

    private static int levenshtein(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[] prev = new int[n + 1];
        int[] cur = new int[n + 1];
        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= m; i++) {
            cur[0] = i;
            for (int j = 1; j <= n; j++) {
                int cost = s.charAt(i - 1) == t.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] tmp = prev;
            prev = cur;
            cur = tmp;
        }
        return prev[n];
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
