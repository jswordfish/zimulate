package com.v2.competency.management.elevanlabs.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConversationConfig {
    private Agent agent;

    private Tts tts;

    private Asr asr;

    private Turn turn;

    @JsonProperty("conversation_termination")
    private ConversationTermination conversationTermination;

    private Interruption interruption;

    @JsonProperty("knowledge_base")
    private List<KnowledgeBase> knowledgeBase;
}