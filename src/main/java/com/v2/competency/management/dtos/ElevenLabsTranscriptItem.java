package com.v2.competency.management.dtos;

public class ElevenLabsTranscriptItem {
	
	private String role;
    private String message;
    private Double timeInCallSecs;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getTimeInCallSecs() {
        return timeInCallSecs;
    }

    public void setTimeInCallSecs(Double timeInCallSecs) {
        this.timeInCallSecs = timeInCallSecs;
    }

}
