package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.SimpleChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemanticKernelStudyControllerTest {

    @Mock
    private SimpleChatService simpleChatService;

    @InjectMocks
    private SemanticKernelStudyController controller;

    @Test
    void processMessageByKernelFunction_InvalidInput_ReturnsBadRequest() {
        // Given
        Map<String, String> request = Map.of();

        // When
        Mono<ResponseEntity<Map<String, Object>>> resultMono = controller.processMessageByKernelFunction(request);
        ResponseEntity<Map<String, Object>> response = resultMono.block();

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Input cannot be empty", response.getBody().get("error"));

        verify(simpleChatService, never()).getByKernelFunctionPromptFromSemanticKernel(any(), anyDouble(), anyBoolean());
    }

    @Test
    void processMessageByKernelFunction_ValidInput_ReturnsMonoResponseEntity() {
        // Given
        String input = "Tell me a joke.";
        double temperature = 0.7;
        boolean metadata = true;
        Map<String, String> request = Map.of(
                "input", input,
                "temperature", String.valueOf(temperature),
                "metadata", String.valueOf(metadata)
        );
        Map<String, Object> mockResponse = Map.of("response", "Here's a joke!");

        when(simpleChatService.getByKernelFunctionPromptFromSemanticKernel(input, temperature, metadata))
                .thenReturn(Mono.just(mockResponse));

        // When
        Mono<ResponseEntity<Map<String, Object>>> resultMono = controller.processMessageByKernelFunction(request);
        ResponseEntity<Map<String, Object>> response = resultMono.block();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Here's a joke!", response.getBody().get("response"));

        verify(simpleChatService, times(1))
                .getByKernelFunctionPromptFromSemanticKernel(input, temperature, metadata);
    }

    @Test
    void processMessage_ValidInput_HappyPath() {
        String input = "Hello, AI!";
        List<String> mockResponse = List.of("AI Response 1", "AI Response 2");
        when(simpleChatService.getChatCompletionsByOpenAIClient(input)).thenReturn(mockResponse);

        // Act
        List<String> response = controller.processMessage(Map.of("input", input));

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("AI Response 1", response.get(0));
        verify(simpleChatService, times(1)).getChatCompletionsByOpenAIClient(input);
    }

    @Test
    void processMessage_InvalidInput_ThrowsException() {
        Map<String, String> request = Map.of();

        Exception exception = assertThrows(RuntimeException.class, () -> controller.processMessage(request));
        assertEquals("Invalid input message", exception.getMessage());
    }
}
