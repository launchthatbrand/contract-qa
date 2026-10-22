CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE documents (
    id uuid PRIMARY KEY,
    filename text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE chunks (
    id uuid PRIMARY KEY,
    document_id uuid NOT NULL REFERENCES documents (id),
    position integer NOT NULL,
    content text NOT NULL,
    embedding vector(768) NOT NULL
);

CREATE INDEX chunks_document_idx ON chunks (document_id);
CREATE INDEX chunks_embedding_idx ON chunks USING hnsw (embedding vector_cosine_ops);
