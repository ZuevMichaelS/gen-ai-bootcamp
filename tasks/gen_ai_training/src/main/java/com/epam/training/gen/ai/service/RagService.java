package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.VectorSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final TextSplitter textSplitter;
    private final SimpleVectorActions simpleVectorService;
    private final SimpleChatService simpleChatService;

    private final int DEFAULT_RESPONSES = 10;

    @SneakyThrows
    public String uploadKnowledgeLayer(String file) {
        var filePath = Path.of(file);
        var fileName = filePath.getFileName().toString();
        simpleVectorService.createCollection(fileName);
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        List<String> chunks = textSplitter.split(content);
        chunks.forEach(chunk -> simpleVectorService.processAndSaveText(fileName, chunk));
        log.info("File uploaded.");
        return fileName;
    }

    @SneakyThrows
    public List<String> askQuestion(String collectionName, String question) {
        var topChunks = simpleVectorService.search(question, collectionName, DEFAULT_RESPONSES);

        String prompt = buildPrompt(topChunks, question);
        var answers = simpleChatService.getRequestResponse(prompt);

        log.info("--- Context ---");
        topChunks.forEach(chunk -> log.info(chunk.getText()));
        log.info("\n--- Question ---");
        log.info(question);
        log.info("\n--- Answer ---");
        answers.forEach(log::info);
        return answers;
    }

    private String buildPrompt(List<VectorSearchResponse> contextChunks, String question) {
        StringBuilder sb = new StringBuilder("Based on the following context, answer the question.\n\nContext:\n");
        contextChunks.forEach(chunk -> sb.append(chunk.getText()).append("\n"));
        sb.append("\nQuestion: ").append(question);
        return sb.toString();
    }
}
