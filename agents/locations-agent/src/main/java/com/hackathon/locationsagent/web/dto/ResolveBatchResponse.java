package com.hackathon.locationsagent.web.dto;

import com.hackathon.locationsagent.resolve.ResolvedLocation;

import java.util.List;

public record ResolveBatchResponse(
        List<ResolveBatchItem> results
) {
    public record ResolveBatchItem(
            String query,
            ResolvedLocation resolved
    ) {
    }
}
