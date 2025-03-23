package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.SimpleChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class SemanticKernelStudyController {

    private final SimpleChatService simpleChatService;

    private static final String INPUT_KEY = "input";
    private static final String TEMPERATURE_KEY = "temperature";
    private static final String METADATA_KEY = "metadata";

    @PostMapping("/openai/process-message")
    public List<String> processMessage(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            throw new RuntimeException("Invalid input message");
        }

        return simpleChatService.getChatCompletionsByOpenAIClient(input);
    }

    @PostMapping("/sk/process-message")
    public Mono<ResponseEntity<Map<String, Object>>> processMessageByKernelFunction(@RequestBody Map<String, String> request) {
        double temperature = Double.NaN;
        boolean metadata = Boolean.FALSE;
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        String metadataValue = request.get(METADATA_KEY);
        if (metadataValue != null && !metadataValue.isEmpty()) {
            metadata = Boolean.parseBoolean(metadataValue);
        }
        String temperatureValue = request.get(TEMPERATURE_KEY);
        if (temperatureValue != null && !temperatureValue.isEmpty()) {
            temperature = Double.parseDouble(temperatureValue);
        }

        return simpleChatService.getByKernelFunctionPromptFromSemanticKernel(input, temperature, metadata).map(ResponseEntity::ok);
    }

}
