package com.googlecloud.vertex.ai.communication.dto;

public class FinalSummaryCommunications {

	Double overAllRelevancyScore;

	String overAllObservationsForAllQuestions;
	
	Double overAllFinalScore;

	public Double getOverAllRelevancyScore() {
		return overAllRelevancyScore;
	}

	public void setOverAllRelevancyScore(Double overAllRelevancyScore) {
		this.overAllRelevancyScore = overAllRelevancyScore;
	}

	public String getOverAllObservationsForAllQuestions() {
		return overAllObservationsForAllQuestions;
	}

	public void setOverAllObservationsForAllQuestions(String overAllObservationsForAllQuestions) {
		this.overAllObservationsForAllQuestions = overAllObservationsForAllQuestions;
	}

	public Double getOverAllFinalScore() {
		return overAllFinalScore;
	}

	public void setOverAllFinalScore(Double overAllFinalScore) {
		this.overAllFinalScore = overAllFinalScore;
	}
	
	

}
