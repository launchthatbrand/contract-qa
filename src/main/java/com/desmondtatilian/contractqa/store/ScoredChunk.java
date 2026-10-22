package com.desmondtatilian.contractqa.store;

import java.util.UUID;

public record ScoredChunk(UUID id, int position, String content, double cosineDistance) {
}
