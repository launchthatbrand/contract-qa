package com.desmondtatilian.contractqa.ai;

import java.util.UUID;

public record Passage(UUID chunkId, int position, String content) {
}
