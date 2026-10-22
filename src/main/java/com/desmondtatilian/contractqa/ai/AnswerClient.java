package com.desmondtatilian.contractqa.ai;

import java.util.List;

public interface AnswerClient {

    String answer(String question, List<Passage> passages);
}
