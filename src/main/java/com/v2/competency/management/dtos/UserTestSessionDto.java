package com.v2.competency.management.dtos;

public class UserTestSessionDto {
	
	String user;
	String testName;
	
	Integer timeTakenInMimnutes;
	
	
	Boolean complete = false;
	
	Integer noOfAttempts;
	
	Float percentageMarksRecieved;
	
	Integer totalMarksRecieved;
	
	Integer totalMarks;
	
	String sectionResults;
	
	Boolean pass = false;
	
	
	
	Integer noOfQuestionsAnswered;
	
	String sectionsNoOfQuestionsNotAnswered;
	
	Float weightedScorePercentage;
	
	Integer noOfNonCompliances = 0;
	
	
	
	Boolean subjective  = false;
	
	Boolean markComplete = false;//a
	
	
	String firstName;
	
	String lastName;
	
	Integer attempt;
	
	
	String emailWithAttempt;
	
	String formattedDt;
	
	Boolean survey;
	
	String reviewedForEmail;
	
	Boolean reviewMode = false;
	
	
	String surveyResults;
	
	String dateofTest;

	String formattedWeightedScore;

	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getTestName() {
		return testName;
	}


	public void setTestName(String testName) {
		this.testName = testName;
	}


	public Integer getTimeTakenInMimnutes() {
		return timeTakenInMimnutes;
	}


	public void setTimeTakenInMimnutes(Integer timeTakenInMimnutes) {
		this.timeTakenInMimnutes = timeTakenInMimnutes;
	}


	public Boolean getComplete() {
		return complete;
	}


	public void setComplete(Boolean complete) {
		this.complete = complete;
	}


	public Integer getNoOfAttempts() {
		return noOfAttempts;
	}


	public void setNoOfAttempts(Integer noOfAttempts) {
		this.noOfAttempts = noOfAttempts;
	}


	public Float getPercentageMarksRecieved() {
		return percentageMarksRecieved;
	}


	public void setPercentageMarksRecieved(Float percentageMarksRecieved) {
		this.percentageMarksRecieved = percentageMarksRecieved;
	}


	public Integer getTotalMarksRecieved() {
		return totalMarksRecieved;
	}


	public void setTotalMarksRecieved(Integer totalMarksRecieved) {
		this.totalMarksRecieved = totalMarksRecieved;
	}


	public Integer getTotalMarks() {
		return totalMarks;
	}


	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}


	public String getSectionResults() {
		return sectionResults;
	}


	public void setSectionResults(String sectionResults) {
		this.sectionResults = sectionResults;
	}


	public Boolean getPass() {
		return pass;
	}


	public void setPass(Boolean pass) {
		this.pass = pass;
	}


	public Integer getNoOfQuestionsAnswered() {
		return noOfQuestionsAnswered;
	}


	public void setNoOfQuestionsAnswered(Integer noOfQuestionsAnswered) {
		this.noOfQuestionsAnswered = noOfQuestionsAnswered;
	}


	public String getSectionsNoOfQuestionsNotAnswered() {
		return sectionsNoOfQuestionsNotAnswered;
	}


	public void setSectionsNoOfQuestionsNotAnswered(String sectionsNoOfQuestionsNotAnswered) {
		this.sectionsNoOfQuestionsNotAnswered = sectionsNoOfQuestionsNotAnswered;
	}


	public Float getWeightedScorePercentage() {
		return weightedScorePercentage;
	}


	public void setWeightedScorePercentage(Float weightedScorePercentage) {
		this.weightedScorePercentage = weightedScorePercentage;
	}


	public Integer getNoOfNonCompliances() {
		return noOfNonCompliances;
	}


	public void setNoOfNonCompliances(Integer noOfNonCompliances) {
		this.noOfNonCompliances = noOfNonCompliances;
	}


	public Boolean getSubjective() {
		return subjective;
	}


	public void setSubjective(Boolean subjective) {
		this.subjective = subjective;
	}


	public Boolean getMarkComplete() {
		return markComplete;
	}


	public void setMarkComplete(Boolean markComplete) {
		this.markComplete = markComplete;
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


	public Integer getAttempt() {
		return attempt;
	}


	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}


	public String getEmailWithAttempt() {
		return emailWithAttempt;
	}


	public void setEmailWithAttempt(String emailWithAttempt) {
		this.emailWithAttempt = emailWithAttempt;
	}


	public String getFormattedDt() {
		return formattedDt;
	}


	public void setFormattedDt(String formattedDt) {
		this.formattedDt = formattedDt;
	}


	public Boolean getSurvey() {
		return survey;
	}


	public void setSurvey(Boolean survey) {
		this.survey = survey;
	}


	public String getReviewedForEmail() {
		return reviewedForEmail;
	}


	public void setReviewedForEmail(String reviewedForEmail) {
		this.reviewedForEmail = reviewedForEmail;
	}


	public Boolean getReviewMode() {
		return reviewMode;
	}


	public void setReviewMode(Boolean reviewMode) {
		this.reviewMode = reviewMode;
	}


	public String getSurveyResults() {
		return surveyResults;
	}


	public void setSurveyResults(String surveyResults) {
		this.surveyResults = surveyResults;
	}


	public String getDateofTest() {
		return dateofTest;
	}


	public void setDateofTest(String dateofTest) {
		this.dateofTest = dateofTest;
	}


	public String getFormattedWeightedScore() {
		return formattedWeightedScore;
	}


	public void setFormattedWeightedScore(String formattedWeightedScore) {
		this.formattedWeightedScore = formattedWeightedScore;
	}
	
	

}
