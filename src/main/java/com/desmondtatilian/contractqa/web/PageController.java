package com.desmondtatilian.contractqa.web;

import com.desmondtatilian.contractqa.ask.DocumentService;
import com.desmondtatilian.contractqa.ask.QuestionResult;
import com.desmondtatilian.contractqa.ask.StoredDocument;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PageController {

    private final DocumentService documents;

    public PageController(DocumentService documents) {
        this.documents = documents;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/documents")
    public RedirectView upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a PDF");
        }
        StoredDocument stored = documents.ingest(file.getOriginalFilename(), file.getBytes());
        return new RedirectView("/documents/" + stored.id());
    }

    @GetMapping("/documents/{id}")
    public String document(@PathVariable UUID id, Model model) {
        model.addAttribute("documentId", id);
        return "document";
    }

    @PostMapping("/documents/{id}/questions")
    public String ask(@PathVariable UUID id, @RequestParam String question, Model model) {
        QuestionResult result = documents.ask(id, question);
        model.addAttribute("documentId", id);
        model.addAttribute("question", question);
        model.addAttribute("result", result);
        return "document";
    }
}
