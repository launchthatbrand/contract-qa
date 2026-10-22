package com.desmondtatilian.contractqa.ask;

import java.util.UUID;

public record StoredDocument(UUID id, String filename, int chunkCount) {
}
