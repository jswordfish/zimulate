package com.v2.competency.management.entities;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;

@Entity
public class VFRolePlayTestSession extends Base {
	
String email;
	
	String firstName;
	
	String lastName;
	
	
	/**
	 * same as testname
	 */
	String testIdentifier;
	
	@Lob
	String videoInsightsJson;
	
	@Lob
	String energyGraphJson;
	
	String testName;
	
	String aiAnalysisLink;
	
	Integer attempt;
	
	String videoLink;
	
	String videoUrl;
	
	Float finalScore;
	
	@Lob
	String insightsJson;
	
	@Transient
	RolePlayInsightsDto insightsDto;
	
	Boolean evaluationFailed;
	
	@Lob
	String reviewerInsightsJson;
	
	@Transient
	RolePlayInsightsDto reviewerInsightsDto;
	
	Boolean reviewDone;
	
	
	String  difficultyLevel = RoleplayDifficultyLevel.DEFAULT.getLevel();
	
	String rolePlayPersona;
	
	//'V2'
	String reportsVersion ; 
	
	@Transient
	ObjectMapper mapper = new ObjectMapper();
	
	Long workflowSessionId;
	
	@Transient
	String error;
	
	

	public VFRolePlayTestSession() {
	
	}
	
	

	public VFRolePlayTestSession(String email, String firstName, String lastName, String testName, Integer attempt, String companyId) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.testName = testName;
		this.attempt = attempt;
		this.companyId = companyId;
	}



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

	
	public Float getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(Float finalScore) {
		this.finalScore = finalScore;
	}

	public String getInsightsJson() {
		return insightsJson;
	}

	public void setInsightsJson(String insightsJson) {
		this.insightsJson = insightsJson;
	}

	public RolePlayInsightsDto getInsightsDto() throws JsonParseException, JsonMappingException, IOException {
		if(this.insightsJson != null && this.insightsDto == null) {
			this.insightsDto = mapper.readValue(this.insightsJson.getBytes(), RolePlayInsightsDto.class);
		}
		return insightsDto;
	}

	public void setInsightsDto(RolePlayInsightsDto insightsDto) {
		this.insightsDto = insightsDto;
	}

	public Boolean getEvaluationFailed() {
		return evaluationFailed;
	}

	public void setEvaluationFailed(Boolean evaluationFailed) {
		this.evaluationFailed = evaluationFailed;
	}



	public String getReviewerInsightsJson() {
			
		return reviewerInsightsJson;
	}



	public void setReviewerInsightsJson(String reviewerInsightsJson) {
		this.reviewerInsightsJson = reviewerInsightsJson;
	}



	public RolePlayInsightsDto getReviewerInsightsDto() throws JsonParseException, JsonMappingException, IOException {
			if(this.reviewerInsightsJson != null && this.reviewerInsightsDto == null) {
				this.reviewerInsightsDto = mapper.readValue(this.reviewerInsightsJson.getBytes(), RolePlayInsightsDto.class);
			}
		return reviewerInsightsDto;
	}



	public void setReviewerInsightsDto(RolePlayInsightsDto reviewerInsightsDto) {
		this.reviewerInsightsDto = reviewerInsightsDto;
	}



	public Boolean getReviewDone() {
		return reviewDone;
	}



	public void setReviewDone(Boolean reviewDone) {
		this.reviewDone = reviewDone;
	}



	public String getVideoLink() {
		return videoLink;
	}



	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}




	public String getEnergyGraphJson() {
		return energyGraphJson;
	}



	public void setEnergyGraphJson(String energyGraphJson) {
		this.energyGraphJson = energyGraphJson;
	}



	public String getVideoUrl() {
		return videoUrl;
	}



	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}



	public String getVideoInsightsJson() {
		return videoInsightsJson;
	}



	public void setVideoInsightsJson(String videoInsightsJson) {
		this.videoInsightsJson = videoInsightsJson;
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



	public String getDifficultyLevel() {
		if(this.difficultyLevel == null) {
			return RoleplayDifficultyLevel.DEFAULT.getLevel();
		}
		return difficultyLevel;
	}



	public void setDifficultyLevel(String difficultyLevel) {
		RoleplayDifficultyLevel.valueOf(difficultyLevel);
		this.difficultyLevel = difficultyLevel;
	}



	public String getRolePlayPersona() {
		return rolePlayPersona;
	}



	public void setRolePlayPersona(String rolePlayPersona) {
		this.rolePlayPersona = rolePlayPersona;
	}



	public String getReportsVersion() {
		return reportsVersion;
	}



	public void setReportsVersion(String reportsVersion) {
		this.reportsVersion = reportsVersion;
	}



	public Long getWorkflowSessionId() {
		return workflowSessionId;
	}



	public void setWorkflowSessionId(Long workflowSessionId) {
		this.workflowSessionId = workflowSessionId;
	}



	public String getError() {
		return error;
	}



	public void setError(String error) {
		this.error = error;
	}



	
	

}
