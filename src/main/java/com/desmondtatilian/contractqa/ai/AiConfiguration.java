package com.desmondtatilian.contractqa.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    @ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "none", matchIfMissing = true)
    AnswerClient fakeAnswerClient() {
        return new FakeAnswerClient();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.ai.model.embedding.text", havingValue = "none", matchIfMissing = true)
    EmbeddingClient fakeEmbeddingClient(RetrievalProperties properties) {
        return new FakeEmbeddingClient(properties.dimensions());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "google-genai")
    AnswerClient geminiAnswerClient(ChatClient.Builder builder) {
        return new SpringAiAnswerClient(builder);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.ai.model.embedding.text", havingValue = "google-genai")
    EmbeddingClient geminiEmbeddingClient(EmbeddingModel embeddingModel, RetrievalProperties properties) {
        return new SpringAiEmbeddingClient(embeddingModel, properties.dimensions());
    }
}
