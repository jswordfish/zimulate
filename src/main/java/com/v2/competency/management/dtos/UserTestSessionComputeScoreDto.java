package com.v2.competency.management.dtos;

public class UserTestSessionComputeScoreDto {
	
	String testName;
	
	String testIdentifier;
	
	String email;
	
	Integer attempt;
	
	Double averageScore;
	
	

	public UserTestSessionComputeScoreDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserTestSessionComputeScoreDto(String testName, String testIdentifier, String email, Integer attempt,
			Double averageScore) {
		super();
		this.testName = testName;
		this.testIdentifier = testIdentifier;
		this.email = email;
		this.attempt = attempt;
		this.averageScore = averageScore;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestIdentifier() {
		return testIdentifier;
	}

	public void setTestIdentifier(String testIdentifier) {
		this.testIdentifier = testIdentifier;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public Double getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Double averageScore) {
		this.averageScore = averageScore;
	}
	
	

}
