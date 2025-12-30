package com.v2.competency.management.entities;

public enum Test_Type {
	
	
	
	MCQ_SURVEY("MCQ_SURVEY"), MCQ("MCQ"), SUBJECTIVE("SUBJECTIVE"), SURVEY_360_FEEDBACK("SURVEY_360_FEEDBACK"), ROLE_PLAY("ROLE_PLAY"), QUIZ("QUIZ"), MCQ_SCENARIO("MCQ_SCENARIO");
	
	String testType;
	
	private Test_Type(String testType) {
		this.testType = testType;
	}

	public String getTestType() {
		return testType;
	}
	
	

}
