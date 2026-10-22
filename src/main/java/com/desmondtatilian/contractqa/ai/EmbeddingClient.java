package com.desmondtatilian.contractqa.ai;

public interface EmbeddingClient {

    float[] embed(String text);

    int dimensions();
}
