package com.epam.training.gen.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiModel {

    private String reference;
    private String description;
    @JsonProperty(value = "description_keywords")
    private List<String> descriptionKeywords;
    private String status;
    @Override
    public String toString() {
        return "Elements{" +
                "reference='" + reference + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
