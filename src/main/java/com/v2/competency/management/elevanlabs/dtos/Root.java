package com.v2.competency.management.elevanlabs.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Root {
    private String name;

    @JsonProperty("conversation_config")
    private ConversationConfig conversationConfig;
}