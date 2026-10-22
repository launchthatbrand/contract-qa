# Contract Q&A

A question is answered from the passages stored for that PDF. The model never sees a question that has no nearby passage, and every returned answer cites the passages that were retrieved.

The default process embeds text with a deterministic local vector and answers by returning the top passage. That is what the tests run. Set `SPRING_PROFILES_ACTIVE=gemini` to switch both calls to Spring AI on Gemini: `gemini-embedding-001` for vectors and `gemini-3.5-flash` for the answer. The database column is `vector(768)`. Gemini's embedding model is asked for 768 dimensions, which is one of the sizes it supports. The Gemini profile needs `GEMINI_API_KEY` from Google AI Studio. `GEMINI_CHAT_MODEL` overrides the chat model.

A database created with the earlier 1024-dimension column has to be recreated. `docker compose down -v` drops it.

## Run

```bash
docker compose up -d
mvn spring-boot:run
```

Gemini:

```bash
GEMINI_API_KEY=... SPRING_PROFILES_ACTIVE=gemini mvn spring-boot:run
```

Open http://localhost:8080, upload a PDF, and ask a question.

The cluster copy is https://contract-qa.apps.onpoint.host. `GEMINI_API_KEY` and the database password live in the `contract-qa-env` secret in the `launchthat` namespace. They are not in this repository. A push to `main` builds `ghcr.io/launchthatbrand/contract-qa` and rolls the deployment on the k3s node.

`POST /api/documents` accepts a multipart file named `file`. `POST /api/questions` accepts `{"documentId","question"}` and returns `answer`, `found`, and `citations`.
