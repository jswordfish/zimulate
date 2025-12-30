package com.googlecloud.vertex.ai.dto;

public class RelevancyScoreForAnswerDto {
	
	String relevancyOfAnswerObservations = "NA";
	
	Integer relevanceOfAnswerScoreInPercent = 30 ;

	public String getRelevancyOfAnswerObservations() {
		return relevancyOfAnswerObservations;
	}

	public void setRelevancyOfAnswerObservations(String relevancyOfAnswerObservations) {
		this.relevancyOfAnswerObservations = relevancyOfAnswerObservations;
	}

	public Integer getRelevanceOfAnswerScoreInPercent() {
		return relevanceOfAnswerScoreInPercent;
	}

	public void setRelevanceOfAnswerScoreInPercent(Integer relevanceOfAnswerScoreInPercent) {
		this.relevanceOfAnswerScoreInPercent = relevanceOfAnswerScoreInPercent;
	}
	
	

}
