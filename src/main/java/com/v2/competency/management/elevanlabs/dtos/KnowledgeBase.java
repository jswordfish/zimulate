package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class KnowledgeBase {
    private String type;

    @JsonProperty("documentation_id")
    private String documentationId;
}
