package com.desmondtatilian.contractqa.ai;

import com.google.genai.Client;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("gemini")
public class GeminiConfiguration {

    @Bean
    ChatModel geminiChatModel(
            @Value("${spring.ai.google.genai.api-key}") String apiKey,
            @Value("${spring.ai.google.genai.chat.options.model}") String model) {
        Client client = Client.builder().apiKey(apiKey).build();
        return GoogleGenAiChatModel.builder()
                .genAiClient(client)
                .defaultOptions(GoogleGenAiChatOptions.builder().model(model).temperature(0.2).build())
                .build();
    }

    @Bean
    EmbeddingModel geminiEmbeddingModel(
            @Value("${spring.ai.google.genai.embedding.api-key}") String apiKey,
            @Value("${spring.ai.google.genai.embedding.text.options.model}") String model,
            @Value("${spring.ai.google.genai.embedding.text.options.dimensions}") int dimensions) {
        GoogleGenAiEmbeddingConnectionDetails connection = GoogleGenAiEmbeddingConnectionDetails.builder()
                .apiKey(apiKey)
                .build();
        GoogleGenAiTextEmbeddingOptions options = GoogleGenAiTextEmbeddingOptions.builder()
                .model(model)
                .dimensions(dimensions)
                .build();
        return new GoogleGenAiTextEmbeddingModel(connection, options);
    }
}
