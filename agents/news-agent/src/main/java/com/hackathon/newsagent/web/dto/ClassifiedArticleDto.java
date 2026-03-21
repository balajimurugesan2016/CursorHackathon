package com.hackathon.newsagent.web.dto;

import java.util.List;

public record ClassifiedArticleDto(
        String uri,
        String title,
        String body,
        String url,
        String date,
        String dateTime,
        List<CategoryAssignmentDto> categories,
        ShippingRouteImpactDto shippingRouteImpact
) {
}
