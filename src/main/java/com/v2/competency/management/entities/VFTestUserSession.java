package com.v2.competency.management.entities;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.googlecloud.vertex.ai.communication.dto.AITestResponseCommunication;
import com.googlecloud.vertex.ai.dto.AITestResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class VFTestUserSession extends Base {
	
	String email;
	
	String firstName;
	
	String lastName;
	
	String testIdentifier;
	
	String testName;
	
	String aiAnalysisLink;
	
	Integer attempt;
	
	@Lob
	String aiAnalysisJson;
	
	@Lob
	String aiAnalysisCommunicationJson;
	
	
	
	@Transient
	AITestResponse testResponse;
	
	@Transient
	AITestResponseCommunication testResponseCommunication;
	
	Float finalScore;
	
	@Transient
	Set<String> skillsAssociatedWithTest;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTestIdentifier() {
		return testIdentifier;
	}

	public void setTestIdentifier(String testIdentifier) {
		this.testIdentifier = testIdentifier;
	}

	public String getAiAnalysisLink() {
		return aiAnalysisLink;
	}

	public void setAiAnalysisLink(String aiAnalysisLink) {
		this.aiAnalysisLink = aiAnalysisLink;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getAiAnalysisJson() {
		return aiAnalysisJson;
	}

	public void setAiAnalysisJson(String aiAnalysisJson) {
		this.aiAnalysisJson = aiAnalysisJson;
	}

	public AITestResponse getTestResponse() {
		return testResponse;
	}

	public void setTestResponse(AITestResponse testResponse) {
		this.testResponse = testResponse;
	}

	public String getAiAnalysisCommunicationJson() {
		return aiAnalysisCommunicationJson;
	}

	public void setAiAnalysisCommunicationJson(String aiAnalysisCommunicationJson) {
		this.aiAnalysisCommunicationJson = aiAnalysisCommunicationJson;
	}

	public AITestResponseCommunication getTestResponseCommunication() {
		return testResponseCommunication;
	}

	public void setTestResponseCommunication(AITestResponseCommunication testResponseCommunication) {
		this.testResponseCommunication = testResponseCommunication;
	}

	public Float getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Float finalScore) {
		this.finalScore = finalScore;
	}

	public Set<String> getSkillsAssociatedWithTest() {
		return skillsAssociatedWithTest;
	}

	public void setSkillsAssociatedWithTest(Set<String> skillsAssociatedWithTest) {
		this.skillsAssociatedWithTest = skillsAssociatedWithTest;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(attempt, email, testIdentifier, getCompanyId());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VFRolePlayTestSession other = (VFRolePlayTestSession) obj;
		return Objects.equals(attempt, other.attempt) && Objects.equals(email, other.email)
				&& Objects.equals(testIdentifier, other.testIdentifier) && Objects.equals(getCompanyId(), other.getCompanyId());
	}

}
