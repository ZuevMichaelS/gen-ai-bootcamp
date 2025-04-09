package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.plugins.GeoLocationPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NavigationService {

    private final ChatCompletionService chatCompletionService;
    private static final String PROMPT = """
                From this coordinates
                Latitude: {{GeoLocation.getCurrentLatitude precision="%d"}}
                and Longitude: {{GeoLocation.getCurrentLongitude precision="%d"}}
                calculate the approximate sailing time if the speed is 9 knots with a tailwind.
                To this locations %s.
                Provide response as list from closest object in format:  #  NAME  Distance Time
            """;
    private static final String PROMPT_OBJECT_LIST = """
                Give me a list of 3 nearest %s with coordinates
                to this coordinates
                Latitude: {{GeoLocation.getCurrentLatitude precision="%d"}}
                and Longitude: {{GeoLocation.getCurrentLongitude precision="%d"}}.
            """;

    private final int current_precision = 3;

    public List<String> processWithModels(String request) {
        Kernel kernel = getKernel();

        KernelFunction<String> getObjectsPrompt = KernelFunction.<String>createFromPrompt(
                        String.format(PROMPT_OBJECT_LIST, request, current_precision, current_precision))
                .build();

        var objectsResponse = getObjectsPrompt.invokeAsync(kernel).block();

        KernelFunction<String> prompt = KernelFunction.<String>createFromPrompt(
                        String.format(PROMPT, current_precision, current_precision, objectsResponse.getResult()))
                .build();

        var response = prompt.invokeAsync(kernel).block();

        return splitString(response.getResult());
    }

    private KernelPlugin getPlugin() {
        return KernelPluginFactory.createFromObject(new GeoLocationPlugin(), "GeoLocation");
    }

    private Kernel getKernel() {
        return Kernel.builder()
                .withPlugin(getPlugin())
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    private List<String> splitString(String initialString) {
        return initialString == null || initialString.isEmpty()
                ? Collections.emptyList()
                : Arrays.stream(initialString.split("\n")).toList();
    }
}
