package com.v2.competency.management.entities;

import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;

//@Entity
public class VFRolePlayTestSessionWithVideoDJ extends Base {
	
String email;
	
	String firstName;
	
	String lastName;
	
	/**
	 * same as testname
	 */
	String testIdentifier;
	
	String testName;
	
	String aiAnalysisLink;
	
	Integer attempt;
	
	MultipartFile video;
	
	
	
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
	
	@Transient
	ObjectMapper mapper = new ObjectMapper();

	public VFRolePlayTestSessionWithVideoDJ() {
	
	}
	
	

	public VFRolePlayTestSessionWithVideoDJ(String email, String firstName, String lastName, String testName, Integer attempt, String companyId, MultipartFile video) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.testName = testName;
		this.attempt = attempt;
		this.companyId = companyId;
		this.video = video;
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



	public MultipartFile getVideo() {
		return video;
	}



	public void setVideo(MultipartFile video) {
		this.video = video;
	}



	


	
	
	
	

}
