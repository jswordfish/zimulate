package com.v2.competency.management.dtos;

public class MCQScoreForUserDto {
	String email;
	
	String firstName;
	
	String lastName;
	
	String testIdentifier;
	
	Double averageScore;
	
	Long count;
	
	Integer attempt;
	
	

	public MCQScoreForUserDto() {
		super();
		// TODO Auto-generated constructor stub
	}

//	public MCQScoreForUserDto(String email, String firstName, String lastName, String testIdentifier, Double averageScore,
//			Long count) {
//		super();
//		this.email = email;
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.testIdentifier = testIdentifier;
//		this.averageScore = averageScore;
//		this.count = count;
//	}
	
	

	public MCQScoreForUserDto(String email, String firstName, String lastName, String testIdentifier, Double averageScore,
			Long count, Integer attempt) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.testIdentifier = testIdentifier;
		this.averageScore = averageScore;
		this.count = count;
		this.attempt = attempt;
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

	

	public Double getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Double averageScore) {
		this.averageScore = averageScore;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getTestIdentifier() {
		return testIdentifier;
	}

	public void setTestIdentifier(String testIdentifier) {
		this.testIdentifier = testIdentifier;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	

}
