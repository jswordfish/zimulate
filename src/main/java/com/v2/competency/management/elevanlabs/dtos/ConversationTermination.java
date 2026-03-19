package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConversationTermination {
    @JsonProperty("max_duration_seconds")
    private Integer maxDurationSeconds;

    @JsonProperty("termination_timeout_seconds")
    private Integer terminationTimeoutSeconds;
}
