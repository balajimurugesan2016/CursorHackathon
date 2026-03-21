package com.hackathon.newsagent.config;

import com.hackathon.newsagent.ai.NewsClassifierAiService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

	@Bean
	public NewsClassifierAiService newsClassifierAiService(ChatLanguageModel chatLanguageModel) {
		return AiServices.create(NewsClassifierAiService.class, chatLanguageModel);
	}
}
