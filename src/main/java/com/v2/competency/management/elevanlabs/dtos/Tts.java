package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tts {
    @JsonProperty("model_id")
    private String modelId;

    @JsonProperty("voice_id")
    private String voiceId;
}