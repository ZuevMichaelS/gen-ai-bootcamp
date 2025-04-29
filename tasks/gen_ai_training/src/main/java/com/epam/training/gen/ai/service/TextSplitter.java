package com.epam.training.gen.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextSplitter {
    private static final int MAX_CHUNK_SIZE = 300;
    private static final int CHUNK_OVERLAP = 50;
    private static final String TEXT_SPLITTING_PATTERN = "(?<=[.!?])\\s+";

    public List<String> split(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split(TEXT_SPLITTING_PATTERN);

        StringBuilder chunk = new StringBuilder();
        for (String sentence : sentences) {
            if (chunk.length() + sentence.length() > MAX_CHUNK_SIZE) {
                chunks.add(chunk.toString());
                int overlapStart = Math.max(0, chunk.length() - CHUNK_OVERLAP);
                chunk = new StringBuilder(chunk.substring(overlapStart)).append(sentence);
            } else {
                chunk.append(sentence).append(" ");
            }
        }
        if (!chunk.isEmpty()) {
            chunks.add(chunk.toString());
        }
        return chunks;
    }
}
