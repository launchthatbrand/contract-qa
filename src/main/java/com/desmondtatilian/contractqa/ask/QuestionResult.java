package com.desmondtatilian.contractqa.ask;

import java.util.List;

public record QuestionResult(String answer, boolean found, List<Citation> citations) {

    public static QuestionResult notFound() {
        return new QuestionResult("The document does not contain an answer to that question.", false, List.of());
    }
}
