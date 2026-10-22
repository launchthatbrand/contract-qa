package com.desmondtatilian.contractqa.ingest;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TextChunker {

    static final int TARGET_CHARS = 500;

    public List<String> chunk(String text) {
        String[] words = text.replace("\r\n", "\n").trim().split("\\s+");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (current.length() + word.length() + 1 > TARGET_CHARS && !current.isEmpty()) {
                chunks.add(current.toString());
                current.setLength(0);
            }
            if (!current.isEmpty()) {
                current.append(' ');
            }
            current.append(word);
        }
        if (!current.isEmpty()) {
            chunks.add(current.toString());
        }
        return chunks;
    }
}
