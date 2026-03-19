package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Turn {
    @JsonProperty("turn_timeout")
    private Integer turnTimeout;

    private String mode;
}
