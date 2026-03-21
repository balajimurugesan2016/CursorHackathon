package com.hackathon.newsagent.service;

import com.hackathon.newsagent.classification.ArticleClassifier;
import com.hackathon.newsagent.classification.CategoryScore;
import com.hackathon.newsagent.client.NewsApiClient;
import com.hackathon.newsagent.config.ClassificationProperties;
import com.hackathon.newsagent.model.news.ArticleJson;
import com.hackathon.newsagent.web.dto.AgentRunResponse;
import com.hackathon.newsagent.web.dto.CategoryAssignmentDto;
import com.hackathon.newsagent.web.dto.ClassifiedArticleDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsClassificationService {

    private final NewsApiClient newsApiClient;
    private final ArticleClassifier classifier;
    private final ClassificationProperties classificationProps;

    public NewsClassificationService(
            NewsApiClient newsApiClient,
            ArticleClassifier classifier,
            ClassificationProperties classificationProps
    ) {
        this.newsApiClient = newsApiClient;
        this.classifier = classifier;
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
            classified.add(new ClassifiedArticleDto(
                    article.uri(),
                    article.title(),
                    article.body(),
                    article.url(),
                    article.date(),
                    article.dateTime(),
                    assignments
            ));
        }
        return new AgentRunResponse(articles.size(), classified);
    }
}
