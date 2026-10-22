package com.desmondtatilian.contractqa.ai;

import java.util.List;

public final class FakeAnswerClient implements AnswerClient {

    @Override
    public String answer(String question, List<Passage> passages) {
        return passages.getFirst().content();
    }
}
