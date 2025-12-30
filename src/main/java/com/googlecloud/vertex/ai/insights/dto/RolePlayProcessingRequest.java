package com.googlecloud.vertex.ai.insights.dto;

import java.util.Objects;

import com.v2.competency.management.entities.VFRolePlayTestSession;

public class RolePlayProcessingRequest {
    private VFRolePlayTestSession session;
    private String transcript;

    public RolePlayProcessingRequest(VFRolePlayTestSession session, String transcript) {
        this.session = session;
        this.transcript = transcript;
    }

	public VFRolePlayTestSession getSession() {
		return session;
	}

	public void setSession(VFRolePlayTestSession session) {
		this.session = session;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	@Override
	public int hashCode() {
		return Objects.hash(session, transcript);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RolePlayProcessingRequest other = (RolePlayProcessingRequest) obj;
		return Objects.equals(session, other.session);
	}
    
    
    
    
}