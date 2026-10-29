package com.v2.competency.management.dtos;

import java.util.List;

public class ElevenLabsConversationResponse {
	
	private String agentId;
    private String status;
    private String conversationId;
    private List<ElevenLabsTranscriptItem> transcript;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<ElevenLabsTranscriptItem> getTranscript() {
        return transcript;
    }

    public void setTranscript(List<ElevenLabsTranscriptItem> transcript) {
        this.transcript = transcript;
    }

}
