package com.desmondtatilian.contractqa.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record RetrievalProperties(Retrieval retrieval, Embedding embedding) {

    public int limit() {
        return retrieval.limit();
    }

    public double maxCosineDistance() {
        return retrieval.maxCosineDistance();
    }

    public int dimensions() {
        return embedding.dimensions();
    }

    public record Retrieval(int limit, double maxCosineDistance) {
    }

    public record Embedding(int dimensions) {
    }
}
