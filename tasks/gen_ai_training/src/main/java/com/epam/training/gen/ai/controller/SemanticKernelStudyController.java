package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.model.AiModel;
import com.epam.training.gen.ai.model.VectorSearchResponse;
import com.epam.training.gen.ai.service.HistoryChatService;
import com.epam.training.gen.ai.service.ModelChangeService;
import com.epam.training.gen.ai.service.ModelService;
import com.epam.training.gen.ai.service.NavigationService;
import com.epam.training.gen.ai.service.RagService;
import com.epam.training.gen.ai.service.SimpleChatService;
import com.epam.training.gen.ai.service.SimpleVectorActions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class SemanticKernelStudyController {

    private final SimpleChatService simpleChatService;
    private final HistoryChatService historyChatService;
    private final ModelService modelService;
    private final ModelChangeService modelChangeService;
    private final NavigationService navigationService;
    private final SimpleVectorActions simpleVectorActions;
    private final RagService ragService;

    private static final String INPUT_KEY = "input";
    private static final String TEMPERATURE_KEY = "temperature";
    private static final String METADATA_KEY = "metadata";
    private static final String MODEL_KEY = "model";
    private static final String COLLECTION_KEY = "collection";

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

    @PostMapping("/sk/history")
    public Mono<ResponseEntity<Map<String, Object>>> processMessageWithHistory(@RequestBody Map<String, String> request) {
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

        return historyChatService.processWithHistory(input, temperature, metadata).map(ResponseEntity::ok);
    }

    @GetMapping("/dial/model")
    public List<AiModel> getModels() {
        return modelService.getModel();
    }

    @PostMapping("/sk/models")
    public Mono<ResponseEntity<Map<String, Object>>> processMessageWithDifferentModels(@RequestBody Map<String, String> request) {
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

        String modelValue = request.get(MODEL_KEY);
        if (modelValue != null && !modelValue.isEmpty()) {
            return modelChangeService.processWithModels(input, modelValue, temperature, metadata).map(ResponseEntity::ok);
        } else {
            return historyChatService.processWithHistory(input, temperature, metadata).map(ResponseEntity::ok);
        }
    }

    @PostMapping("/sk/plugins")
    public Mono<ResponseEntity<Map<String, Object>>> processWithPlugin(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        return Mono.fromCallable(() -> {
            List<String> result = navigationService.processWithModels(input);
            return ResponseEntity.ok(Map.of("response:", Objects.requireNonNull(result)));
        });
    }

    @PostMapping("/embeddings")
    public Mono<ResponseEntity<Map<String, Object>>> getEmbeddings(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        return Mono.fromCallable(() -> {
            List<EmbeddingItem> result = simpleVectorActions.getEmbeddings(input);
            return ResponseEntity.ok(Map.of("response:", Objects.requireNonNull(result)));
        });
    }

    @PostMapping("/embeddings/save")
    public Mono<ResponseEntity<Map<String, Object>>> saveEmbeddings(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        return Mono.fromCallable(() -> {
            simpleVectorActions.processAndSaveText(SimpleVectorActions.DEFAULT_COLLECTION_NAME, input);
            return ResponseEntity.ok(Map.of("response:", "saved"));
        });
    }

    @PostMapping("/embeddings/search")
    public Mono<ResponseEntity<Map<String, String>>> searchEmbeddings(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        return Mono.fromCallable(() -> {
            List<VectorSearchResponse> result = simpleVectorActions.search(input, SimpleVectorActions.DEFAULT_COLLECTION_NAME, 3);
            var response = result.stream()
                    .collect(Collectors.toMap(VectorSearchResponse::getUuid, VectorSearchResponse::getText));
            return ResponseEntity.ok(response);
        });
    }

    @PostMapping("/rag/load")
    public ResponseEntity<Map<String, String>> loadSource(@RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty"));
        }
        var collection = ragService.uploadKnowledgeLayer(input);
        return ResponseEntity.ok().body(Map.of(COLLECTION_KEY, collection));
    }

    @PostMapping("/rag/question")
    public ResponseEntity<Map<String, String>> askQuestion(
            @RequestBody Map<String, String> request) {
        String input = request.get(INPUT_KEY);
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty"));
        }
        String collection = request.get(COLLECTION_KEY);
        if (collection == null || collection.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "collection cannot be empty"));
        }
        var response = ragService.askQuestion(collection, input);
        return  ResponseEntity.ok().body(Map.of(COLLECTION_KEY, collection, "response", String.join("\n", response)));
    }
}
