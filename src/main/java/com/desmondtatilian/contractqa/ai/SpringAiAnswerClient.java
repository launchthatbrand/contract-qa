package com.desmondtatilian.contractqa.ai;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;

public final class SpringAiAnswerClient implements AnswerClient {

    private static final String SYSTEM = """
            Answer only from the numbered passages. If they do not contain the answer, say that the document does not contain it. Do not add facts from outside the passages.
            """;

    private final ChatClient chatClient;

    public SpringAiAnswerClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String answer(String question, List<Passage> passages) {
        StringBuilder prompt = new StringBuilder();
        for (int i = 0; i < passages.size(); i++) {
            Passage passage = passages.get(i);
            prompt.append("Passage ").append(i + 1).append(": ").append(passage.content()).append("\n");
        }
        prompt.append("\nQuestion: ").append(question);
        return chatClient.prompt().system(SYSTEM).user(prompt.toString()).call().content();
    }
}
