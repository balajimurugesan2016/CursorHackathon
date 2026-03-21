package com.hackathon.locationsagent.resolve;

public record ResolvedLocation(
        String query,
        String matchedName,
        String placeType,
        double latitude,
        double longitude,
        MatchKind matchKind,
        double confidence
) {
}
