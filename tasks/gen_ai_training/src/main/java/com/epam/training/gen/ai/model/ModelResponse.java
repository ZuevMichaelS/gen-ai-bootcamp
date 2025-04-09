package com.epam.training.gen.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelResponse {

    private List<AiModel> data;

    public List<AiModel> getData() {
        return data;
    }

    public void setData(List<AiModel> data) {
        this.data = data;
    }
}
