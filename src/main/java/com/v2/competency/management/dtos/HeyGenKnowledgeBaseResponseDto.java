package com.v2.competency.management.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class HeyGenKnowledgeBaseResponseDto {
	private int code;
    private String message;
    private Data data;

    // --- Getters and Setters ---
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    // --- Nested Static Class for "data" ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        
        @JsonProperty("id") // Maps JSON field to Java field
        private String knowledgeBaseId;

        private String name;
        
        @JsonProperty("access_level")
        private String accessLevel;

        // --- Getters and Setters ---
        public String getKnowledgeBaseId() { return knowledgeBaseId; }
        public void setKnowledgeBaseId(String knowledgeBaseId) { this.knowledgeBaseId = knowledgeBaseId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAccessLevel() { return accessLevel; }
        public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    }
}
