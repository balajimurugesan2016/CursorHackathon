package com.hackathon.newsagent.web.dto;

import java.util.List;

public record AgentRunResponse(
        int articleCount,
        List<ClassifiedArticleDto> articles
) {
}
