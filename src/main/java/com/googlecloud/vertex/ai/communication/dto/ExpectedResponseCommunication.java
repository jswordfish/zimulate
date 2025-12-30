package com.googlecloud.vertex.ai.communication.dto;

import java.util.ArrayList;
import java.util.List;

public class ExpectedResponseCommunication {

	
	List<QuestionSpecific> answers = new ArrayList<>();
	
	FinalSummaryCommunications finalSummary = new FinalSummaryCommunications();

	public List<QuestionSpecific> getAnswers() {
		return answers;
	}

	public void setAnswers(List<QuestionSpecific> answers) {
		this.answers = answers;
	}

	public FinalSummaryCommunications getFinalSummary() {
		return finalSummary;
	}

	public void setFinalSummary(FinalSummaryCommunications finalSummary) {
		this.finalSummary = finalSummary;
	}
	
	
}
