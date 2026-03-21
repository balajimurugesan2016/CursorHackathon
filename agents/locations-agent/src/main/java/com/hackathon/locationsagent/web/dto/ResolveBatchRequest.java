package com.hackathon.locationsagent.web.dto;

import java.util.List;

public record ResolveBatchRequest(
        List<String> queries
) {
}
