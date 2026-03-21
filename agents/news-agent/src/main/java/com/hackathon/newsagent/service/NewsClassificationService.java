package com.hackathon.newsagent.service;

import com.hackathon.newsagent.classification.ArticleClassifier;
import com.hackathon.newsagent.classification.CategoryScore;
import com.hackathon.newsagent.classification.ShippingRouteImpactScorer;
import com.hackathon.newsagent.client.NewsApiClient;
import com.hackathon.newsagent.config.ClassificationProperties;
import com.hackathon.newsagent.model.news.ArticleJson;
import com.hackathon.newsagent.web.dto.AgentRunResponse;
import com.hackathon.newsagent.web.dto.CategoryAssignmentDto;
import com.hackathon.newsagent.web.dto.ClassifiedArticleDto;
import com.hackathon.newsagent.web.dto.ShippingRouteImpactDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsClassificationService {

    private final NewsApiClient newsApiClient;
    private final ArticleClassifier classifier;
    private final ShippingRouteImpactScorer shippingRouteImpactScorer;
    private final ClassificationProperties classificationProps;

    public NewsClassificationService(
            NewsApiClient newsApiClient,
            ArticleClassifier classifier,
            ShippingRouteImpactScorer shippingRouteImpactScorer,
            ClassificationProperties classificationProps
    ) {
        this.newsApiClient = newsApiClient;
        this.classifier = classifier;
        this.shippingRouteImpactScorer = shippingRouteImpactScorer;
        this.classificationProps = classificationProps;
    }

    public AgentRunResponse runAgent() {
        List<ArticleJson> articles = newsApiClient.fetchArticles();
        List<ClassifiedArticleDto> classified = new ArrayList<>();
        for (ArticleJson article : articles) {
            List<CategoryScore> scores = classifier.classify(article.title(), article.body());
            List<CategoryAssignmentDto> assignments = new ArrayList<>();
            for (CategoryScore s : scores) {
                if (s.score() < classificationProps.minScore()) {
                    continue;
                }
                if (assignments.size() >= classificationProps.maxCategoriesPerArticle()) {
                    break;
                }
                assignments.add(new CategoryAssignmentDto(
                        s.category().name(),
                        s.category().displayName(),
                        s.category().description(),
                        s.score(),
                        s.matchedSignals()
                ));
            }
            ShippingRouteImpactDto routeImpact =
                    shippingRouteImpactScorer.score(article.title(), article.body());
            classified.add(new ClassifiedArticleDto(
                    article.uri(),
                    article.title(),
                    article.body(),
                    article.url(),
                    article.date(),
                    article.dateTime(),
                    assignments,
                    routeImpact
            ));
        }
        return new AgentRunResponse(articles.size(), classified);
    }
}
