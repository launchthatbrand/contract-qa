package com.desmondtatilian.contractqa.ask;

import java.util.UUID;

public record Citation(UUID chunkId, int position, String excerpt) {
}
