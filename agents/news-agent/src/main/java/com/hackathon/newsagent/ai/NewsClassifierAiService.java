package com.hackathon.newsagent.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface NewsClassifierAiService {

	String TOPICS = """
		Geopolitical Unrest & Security
		Trade Policy and Tariffs
		Environmental & Natural Disasters
		Infrastructure & Logistics Disruptions
		Raw Material & Resource Scarcity
		Technology & Cybersecurity
		Corporate Restructuring
		""";

	@SystemMessage("""
		You are a supply-chain risk analyst. Classify news articles into one or more of these topics:
		""" + TOPICS + """
		
		You MUST extract and return ALL locations where the news is happening as a JSON array. Include:
		- City names (e.g. Singapore, Rotterdam, Shanghai, Hong Kong, Hamburg)
		- Regions (e.g. Strait of Malacca, Suez Canal, Panama Canal)
		- Countries or areas mentioned in the article body
		When enterprise cities are provided, prefer matching those when the article mentions them.
		Extract every distinct location mentioned. Never return an empty locations array.
		
		Respond with ONLY valid JSON in this exact format (no markdown, no extra text):
		{"title":"...","locations":["...","...","..."],"topics":["..."]}
		""")
	@UserMessage("""
		Enterprise cities (reference): {{cities}}
		
		Article title: {{title}}
		Article body: {{body}}
		
		Classify and extract all locations. Return ONLY the JSON object.
		""")
	NewsClassificationResult classify(
			@V("title") String title,
			@V("body") String body,
			@V("cities") String cities);
}
