package com.v2.competency.management.entities;

public enum Test_Grading {

	
AI_GRADING("AI_GRADING"), AI_GRADING_WITH_HUMAN_SUPERVISION("AI_GRADING_WITH_HUMAN_SUPERVISION"), 
HUMAN_GRADING("HUMAN_GRADING"), AUTO_GRADING("AUTO_GRADING");
	
	String grading;
	
	private Test_Grading(String grading) {
		this.grading = grading;
	}

	public String getGrading() {
		return grading;
	}

	
	
}
