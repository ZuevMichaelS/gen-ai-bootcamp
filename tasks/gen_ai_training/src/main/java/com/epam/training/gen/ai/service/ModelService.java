package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.AiModel;
import com.epam.training.gen.ai.model.ModelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelService {

    @Value("${client-openai-key}")
    String apiKey;
    private static final String API_URL = "https://ai-proxy.lab.epam.com/openai/models";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<AiModel> getModel() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Api-Key", apiKey)
                .GET()
                .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var jsonResponse = objectMapper.readValue(response.body(), ModelResponse.class);

                return jsonResponse.getData();
            } else {
                throw new RuntimeException("Failed to generate image: " + response.body());
            }
    }
}
