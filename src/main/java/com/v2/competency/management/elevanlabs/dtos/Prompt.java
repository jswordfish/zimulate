package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Prompt {
    private String prompt;

    @JsonProperty("dynamic_variables")
    private DynamicVariables dynamicVariables;
}