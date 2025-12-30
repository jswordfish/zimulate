package com.v2.competency.management.dtos;

public class QuestionAvailabilityCountDto {
	
	String competency;
	
	String parentCompetency;
	
	String questionType;
	
	Long count;
	
	

	public QuestionAvailabilityCountDto(String competency, String parentCompetency, String questionType, Long count) {
		super();
		this.competency = competency;
		this.parentCompetency = parentCompetency;
		this.questionType = questionType;
		this.count = count;
	}

	public String getCompetency() {
		return competency;
	}

	public void setCompetency(String competency) {
		this.competency = competency;
	}

	public String getParentCompetency() {
		return parentCompetency;
	}

	public void setParentCompetency(String parentCompetency) {
		this.parentCompetency = parentCompetency;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
	

}
