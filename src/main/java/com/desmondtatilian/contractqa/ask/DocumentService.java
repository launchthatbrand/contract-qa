package com.desmondtatilian.contractqa.ask;

import com.desmondtatilian.contractqa.ai.AnswerClient;
import com.desmondtatilian.contractqa.ai.EmbeddingClient;
import com.desmondtatilian.contractqa.ai.Passage;
import com.desmondtatilian.contractqa.ai.RetrievalProperties;
import com.desmondtatilian.contractqa.ingest.PdfTextExtractor;
import com.desmondtatilian.contractqa.ingest.TextChunker;
import com.desmondtatilian.contractqa.store.DocumentStore;
import com.desmondtatilian.contractqa.store.ScoredChunk;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DocumentService {

    private final PdfTextExtractor extractor;
    private final TextChunker chunker;
    private final EmbeddingClient embeddings;
    private final AnswerClient answers;
    private final DocumentStore store;
    private final RetrievalProperties properties;

    public DocumentService(
            PdfTextExtractor extractor,
            TextChunker chunker,
            EmbeddingClient embeddings,
            AnswerClient answers,
            DocumentStore store,
            RetrievalProperties properties) {
        this.extractor = extractor;
        this.chunker = chunker;
        this.embeddings = embeddings;
        this.answers = answers;
        this.store = store;
        this.properties = properties;
    }

    @Transactional
    public StoredDocument ingest(String filename, byte[] pdf) {
        List<String> chunks = chunker.chunk(extractor.extract(pdf));
        if (chunks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The PDF has no extractable text");
        }
        UUID documentId = UUID.randomUUID();
        store.insertDocument(documentId, filename == null || filename.isBlank() ? "document.pdf" : filename);
        int position = 0;
        for (String chunk : chunks) {
            float[] embedding = embeddings.embed(chunk);
            store.insertChunk(UUID.randomUUID(), documentId, position, chunk, embedding);
            position++;
        }
        return new StoredDocument(documentId, filename, chunks.size());
    }

    public QuestionResult ask(UUID documentId, String question) {
        if (!store.documentExists(documentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }
        float[] embedding = embeddings.embed(question);
        List<Passage> passages = store.nearest(documentId, embedding, properties.limit()).stream()
                .filter(chunk -> chunk.cosineDistance() <= properties.maxCosineDistance())
                .map(this::toPassage)
                .toList();
        if (passages.isEmpty()) {
            return QuestionResult.notFound();
        }
        String answer = answers.answer(question, passages);
        List<Citation> citations = passages.stream()
                .map(passage -> new Citation(passage.chunkId(), passage.position(), passage.content()))
                .toList();
        return new QuestionResult(answer, true, citations);
    }

    private Passage toPassage(ScoredChunk chunk) {
        return new Passage(chunk.id(), chunk.position(), chunk.content());
    }
}
