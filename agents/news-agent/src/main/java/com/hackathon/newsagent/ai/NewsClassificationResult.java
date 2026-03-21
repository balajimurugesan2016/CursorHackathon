package com.hackathon.newsagent.ai;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

public record NewsClassificationResult(
	@Description("The title of the news article") String title,
	@Description("All locations where the news is happening: cities, regions, straits, canals. MUST be a non-empty array.") List<String> locations,
	@Description("One or more topic labels from the fixed list that best classify this article") List<String> topics
) {}
