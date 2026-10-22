package com.desmondtatilian.contractqa.ai;

import java.util.Locale;
import java.util.Set;

public final class FakeEmbeddingClient implements EmbeddingClient {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "is", "of", "to", "and", "what", "for", "in", "on");

    private final int dimensions;

    public FakeEmbeddingClient(int dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public float[] embed(String text) {
        float[] vector = new float[dimensions];
        for (String token : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (token.isBlank() || STOP_WORDS.contains(token)) {
                continue;
            }
            int index = Math.floorMod(token.hashCode(), dimensions);
            vector[index] += 1.0f;
        }
        float norm = 0.0f;
        for (float value : vector) {
            norm += value * value;
        }
        if (norm == 0.0f) {
            vector[0] = 1.0f;
            return vector;
        }
        float scale = (float) (1.0 / Math.sqrt(norm));
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= scale;
        }
        return vector;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }
}
