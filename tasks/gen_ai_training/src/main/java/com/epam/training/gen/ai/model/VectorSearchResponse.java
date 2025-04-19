package com.epam.training.gen.ai.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VectorSearchResponse {
    private String uuid;
    private float score;
    private String text;

}
