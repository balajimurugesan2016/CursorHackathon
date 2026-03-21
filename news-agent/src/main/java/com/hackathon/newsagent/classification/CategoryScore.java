package com.hackathon.newsagent.classification;

import java.util.List;

public record CategoryScore(
        SupplyChainCategory category,
        double score,
        List<String> matchedSignals
) {
}
