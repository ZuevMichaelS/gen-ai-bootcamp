package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryChatService {

    private ChatHistory chatHistory;

    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final Map<String, PromptExecutionSettings> promptExecutionsSettingsMap;

    private static final String PROMPT_TEMPLATE = """
            {{$chatHistory}}
            <message role="user">{{$request}}</message>""";

    @Value("${client-openai-deployment-name}")
    private String deploymentOrModelName;

    public Mono<Map<String, Object>> processWithHistory(String request, Double temperature, boolean includeMetadata) {
        getChatHistory(Boolean.FALSE);

        var settingsMap = promptExecutionsSettingsMap;
        if (!temperature.isNaN()) {
            settingsMap = Map.of(deploymentOrModelName, PromptExecutionSettings.builder()
                    .withTemperature(temperature)
                    .build());
        }
        chatHistory.addUserMessage(request);

        return kernel.invokeAsync(getHistoryChatFunction(settingsMap))
                .withArguments(getKernelFunctionArguments(request, chatHistory))
                .withResultType(String.class)
                .map(stringFunctionResult -> {
                    storeResponseInHistory(stringFunctionResult);
                    return createJsonResponse(stringFunctionResult.getResultVariable().isEmpty() ? "failed" : "success", stringFunctionResult, includeMetadata);
                });
    }

    public Mono<Map<String, Object>> processWithHistory(Kernel kernel, ChatHistory chatHistory, String request, Double temperature, boolean includeMetadata) {
        getChatHistory(Boolean.FALSE);

        var settingsMap = promptExecutionsSettingsMap;
        if (!temperature.isNaN()) {
            settingsMap = Map.of(deploymentOrModelName, PromptExecutionSettings.builder()
                    .withTemperature(temperature)
                    .build());
        }
        chatHistory.addUserMessage(request);

        return kernel.invokeAsync(getHistoryChatFunction(settingsMap))
                .withArguments(getKernelFunctionArguments(request, chatHistory))
                .withResultType(String.class)
                .map(stringFunctionResult -> {
                    storeResponseInHistory(stringFunctionResult);
                    return createJsonResponse(stringFunctionResult.getResultVariable().isEmpty() ? "failed" : "success", stringFunctionResult, includeMetadata);
                });
    }

    private void storeResponseInHistory(FunctionResult<String> response) {
        if (response.getResult() != null && !response.getResult().isBlank()) {
            chatHistory.addAssistantMessage(response.getResult());
            log.info("AI answer:" + response.getResult());
        }
        chatHistory.addSystemMessage("Response generation error");
    }

    private KernelFunction<String> getHistoryChatFunction(Map<String, PromptExecutionSettings> executionSettingsMap) {
        return KernelFunction.<String>createFromPrompt(PROMPT_TEMPLATE)
                .withExecutionSettings(executionSettingsMap)
                .build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(String prompt, ChatHistory chatHistory) {
        return KernelFunctionArguments.builder()
                .withVariable("request", prompt)
                .withVariable("chatHistory", chatHistory)
                .build();
    }

    private ChatHistory getChatHistory(boolean dropHistory) {
        if (dropHistory || chatHistory == null) {
            chatHistory = new ChatHistory();
            return chatHistory;
        }
        return chatHistory;
    }

    private Map<String, Object> createJsonResponse(String status, FunctionResult<String> functionResult, boolean includeMetadata) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("response", includeMetadata ? functionResult : splitString(functionResult.getResult()));
        return response;
    }

    private List<String> splitString(String initialString) {
        return initialString == null || initialString.isEmpty()
                ? Collections.emptyList()
                : Arrays.stream(initialString.split("\n")).toList();
    }
}
