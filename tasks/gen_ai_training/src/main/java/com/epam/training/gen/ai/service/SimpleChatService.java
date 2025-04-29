package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionFromPrompt;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
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
public class SimpleChatService {

    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final OpenAIAsyncClient aiAsyncClient;
    private final Map<String, PromptExecutionSettings> promptExecutionsSettingsMap;

    private static final String PROMPT_TEMPLATE = "Question: {{$input}}; Answer:";

    @Value("${client-openai-deployment-name}")
    private String deploymentOrModelName;

    public List<String> getChatCompletionsByOpenAIClient(String request) {
        String prompt = "Respond to the following input: " + request;
        var completions = aiAsyncClient
                .getChatCompletions(
                        deploymentOrModelName,
                        new ChatCompletionsOptions(
                                List.of(new ChatRequestUserMessage(prompt))))
                .block();
        var messages = completions.getChoices().stream()
                .map(c -> c.getMessage().getContent())
                .toList();
        log.info(messages.toString());
        return messages;
    }

    public List<String> getRequestResponse(String request) {
        var completions = aiAsyncClient
                .getChatCompletions(
                        deploymentOrModelName,
                        new ChatCompletionsOptions(
                                List.of(new ChatRequestUserMessage(request))))
                .block();
        var messages = completions.getChoices().stream()
                .map(c -> c.getMessage().getContent())
                .toList();
        log.info(messages.toString());
        return messages;
    }

    public Mono<Map<String, Object>> getByKernelFunctionPromptFromSemanticKernel(
            String request, Double temperature, boolean includeMetadata) {
        var settingsMap = promptExecutionsSettingsMap;
        if (!temperature.isNaN()) {
            settingsMap = Map.of(deploymentOrModelName, PromptExecutionSettings.builder()
                    .withTemperature(temperature)
                    .build());
        }
        var questionAnswerFunction = KernelFunctionFromPrompt.builder()
                .withTemplate(PROMPT_TEMPLATE)
                .withExecutionSettings(settingsMap)
                .build();

        return kernel.invokeAsync(questionAnswerFunction)
                .withArguments(
                        KernelFunctionArguments.builder()
                                .withVariable("input", request)
                                .build())
                .withResultType(String.class)
                .map(stringFunctionResult -> createJsonResponse(stringFunctionResult.getResultVariable().isEmpty() ? "failed" : "success", stringFunctionResult, includeMetadata));
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
