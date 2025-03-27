package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModelChangeService {

    private final HistoryChatService historyChatService;
    private final OpenAIAsyncClient openAIAsyncClient;
    private final KernelPlugin kernelPlugin;

    private ChatHistory chatHistory;
    private String currentModel = "";

    public Mono<Map<String, Object>> processWithModels(String request, String model, Double temperature, boolean includeMetadata) {
        if (!currentModel.equalsIgnoreCase(model)) {
            chatHistory = new ChatHistory();
            currentModel = model;
        }
        return historyChatService.processWithHistory(getKernel(model), chatHistory, request, temperature, includeMetadata);
    }

    public Kernel getKernel(String modelName) {
        var completionService = OpenAIChatCompletion.builder()
                .withModelId(modelName)
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();

        return Kernel.builder()
                .withPlugin(kernelPlugin)
                .withAIService(ChatCompletionService.class, completionService)
                .build();
    }
}
