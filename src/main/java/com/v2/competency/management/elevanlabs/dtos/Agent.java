package com.v2.competency.management.elevanlabs.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Agent {
    private Prompt prompt;

    @JsonProperty("first_message")
    private String firstMessage;

    private String language;

    @JsonProperty("accepted_languages")
    private List<String> acceptedLanguages;

    private String llm;
}