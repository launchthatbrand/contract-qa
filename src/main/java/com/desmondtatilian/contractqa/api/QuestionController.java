package com.desmondtatilian.contractqa.api;

import com.desmondtatilian.contractqa.ask.DocumentService;
import com.desmondtatilian.contractqa.ask.QuestionResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final DocumentService documents;

    public QuestionController(DocumentService documents) {
        this.documents = documents;
    }

    @PostMapping
    public QuestionResult ask(@Valid @RequestBody QuestionRequest request) {
        return documents.ask(request.documentId(), request.question());
    }

    public record QuestionRequest(@NotNull UUID documentId, @NotBlank String question) {
    }
}
