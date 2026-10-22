package com.desmondtatilian.contractqa.store;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentStore {

    private final JdbcTemplate jdbc;

    public DocumentStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insertDocument(UUID id, String filename) {
        jdbc.update(
                "INSERT INTO documents (id, filename, created_at) VALUES (?, ?, now())",
                id,
                filename);
    }

    public void insertChunk(UUID id, UUID documentId, int position, String content, float[] embedding) {
        jdbc.update(
                "INSERT INTO chunks (id, document_id, position, content, embedding) VALUES (?, ?, ?, ?, ?::vector)",
                id,
                documentId,
                position,
                content,
                toVector(embedding));
    }

    public boolean documentExists(UUID documentId) {
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM documents WHERE id = ?",
                Integer.class,
                documentId);
        return count != null && count > 0;
    }

    public List<ScoredChunk> nearest(UUID documentId, float[] embedding, int limit) {
        return jdbc.query(
                """
                SELECT id, position, content, embedding <=> ?::vector AS distance
                FROM chunks
                WHERE document_id = ?
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """,
                (rs, row) -> mapChunk(rs),
                toVector(embedding),
                documentId,
                toVector(embedding),
                limit);
    }

    private static ScoredChunk mapChunk(ResultSet rs) throws SQLException {
        return new ScoredChunk(
                rs.getObject("id", UUID.class),
                rs.getInt("position"),
                rs.getString("content"),
                rs.getDouble("distance"));
    }

    static String toVector(float[] values) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(String.format(Locale.US, "%.6f", values[i]));
        }
        return builder.append(']').toString();
    }
}
