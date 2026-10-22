# Contract Q&A

A question is answered from the passages stored for that PDF. The model never sees a question that has no nearby passage, and every returned answer cites the passages that were retrieved.

The default process embeds text with a deterministic local vector and answers by returning the top passage. That is what the tests run. Set `SPRING_PROFILES_ACTIVE=bedrock` to switch both calls to Spring AI on Amazon Bedrock: Titan Text Embeddings V2 for vectors and the Converse API for the answer. The database column is `vector(1024)`, which matches Titan Text Embeddings V2. The Bedrock profile needs `AWS_REGION` and credentials. `BEDROCK_CHAT_MODEL` selects the chat model and defaults to `amazon.nova-lite-v1:0`.

## Run

```bash
docker compose up -d
mvn spring-boot:run
```

Open http://localhost:8080, upload a PDF, and ask a question.

`POST /api/documents` accepts a multipart file named `file`. `POST /api/questions` accepts `{"documentId","question"}` and returns `answer`, `found`, and `citations`.
