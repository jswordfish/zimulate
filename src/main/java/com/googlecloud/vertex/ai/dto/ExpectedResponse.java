package com.googlecloud.vertex.ai.dto;

public class ExpectedResponse {
	private String questionId;
	  private String questionText;
	  private int relevance;
	  private int depth;
	  private int correctness;
	  private int engagement;
	  private String observation;
	  private int relevanceScore;
	  private double depthScore;
	  private int correctnessScore;
	  private double engagementScore;
	  private int overallScore;
	  
	  String improvementAreas;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public int getRelevance() {
		return relevance;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getCorrectness() {
		return correctness;
	}

	public void setCorrectness(int correctness) {
		this.correctness = correctness;
	}

	public int getEngagement() {
		return engagement;
	}

	public void setEngagement(int engagement) {
		this.engagement = engagement;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public int getRelevanceScore() {
		return relevanceScore;
	}

	public void setRelevanceScore(int relevanceScore) {
		this.relevanceScore = relevanceScore;
	}

	public double getDepthScore() {
		return depthScore;
	}

	public void setDepthScore(double depthScore) {
		this.depthScore = depthScore;
	}

	public int getCorrectnessScore() {
		return correctnessScore;
	}

	public void setCorrectnessScore(int correctnessScore) {
		this.correctnessScore = correctnessScore;
	}

	public double getEngagementScore() {
		return engagementScore;
	}

	public void setEngagementScore(double engagementScore) {
		this.engagementScore = engagementScore;
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
