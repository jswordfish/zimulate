package com.googlecloud.vertex.ai.communication.dto;

public class Interaction {
	
	
	private int purposeOfInteractionScore;
	  private int developmentOfInteractionScore;
	  private int confidenceScore;
	  
	  private int clarityScore;
	  
	  private int accuracyScore;
	 
	  private int overallScore;
	  
	  String improvementAreas;

	public int getPurposeOfInteractionScore() {
		return purposeOfInteractionScore;
	}

	public void setPurposeOfInteractionScore(int purposeOfInteractionScore) {
		this.purposeOfInteractionScore = purposeOfInteractionScore;
	}

	public int getDevelopmentOfInteractionScore() {
		return developmentOfInteractionScore;
	}

	public void setDevelopmentOfInteractionScore(int developmentOfInteractionScore) {
		this.developmentOfInteractionScore = developmentOfInteractionScore;
	}

	public int getConfidenceScore() {
		return confidenceScore;
	}

	public void setConfidenceScore(int confidenceScore) {
		this.confidenceScore = confidenceScore;
	}

	public int getClarityScore() {
		return clarityScore;
	}

	public void setClarityScore(int clarityScore) {
		this.clarityScore = clarityScore;
	}

	public int getAccuracyScore() {
		return accuracyScore;
	}

	public void setAccuracyScore(int accuracyScore) {
		this.accuracyScore = accuracyScore;
	}

	public int getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(int overallScore) {
		this.overallScore = overallScore;
	}

	public String getImprovementAreas() {
		return improvementAreas;
	}

	public void setImprovementAreas(String improvementAreas) {
		this.improvementAreas = improvementAreas;
	}
	  
	  

}
