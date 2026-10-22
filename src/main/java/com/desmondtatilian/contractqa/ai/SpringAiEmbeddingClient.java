package com.desmondtatilian.contractqa.ai;

import org.springframework.ai.embedding.EmbeddingModel;

public final class SpringAiEmbeddingClient implements EmbeddingClient {

    private final EmbeddingModel embeddingModel;
    private final int dimensions;

    public SpringAiEmbeddingClient(EmbeddingModel embeddingModel, int dimensions) {
        this.embeddingModel = embeddingModel;
        this.dimensions = dimensions;
    }

    @Override
    public float[] embed(String text) {
        float[] vector = embeddingModel.embed(text);
        if (vector.length != dimensions) {
            throw new IllegalStateException(
                    "Gemini returned " + vector.length + " dimensions; this database stores " + dimensions);
        }
        return vector;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }
}
