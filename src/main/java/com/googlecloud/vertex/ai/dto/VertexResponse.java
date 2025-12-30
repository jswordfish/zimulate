package com.googlecloud.vertex.ai.dto;

public class VertexResponse {
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
	  
	  String answer;
	  

	  public void setQuestionId(String questionId) {
	    this.questionId = questionId;
	  }
	  public String getQuestionId() {
	    return questionId;
	  }
	  public void setQuestionText(String questionText) {
	    this.questionText = questionText;
	  }
	  public String getQuestionText() {
	    return questionText;
	  }
	  public void setRelevance(int relevance) {
	    this.relevance = relevance;
	  }
	  public int getRelevance() {
	    return relevance;
	  }
	  public void setDepth(int depth) {
	    this.depth = depth;
	  }
	  public int getDepth() {
	    return depth;
	  }
	  public void setCorrectness(int correctness) {
	    this.correctness = correctness;
	  }
	  public int getCorrectness() {
	    return correctness;
	  }
	  public void setEngagement(int engagement) {
	    this.engagement = engagement;
	  }
	  public int getEngagement() {
	    return engagement;
	  }
	  public void setObservation(String observation) {
	    this.observation = observation;
	  }
	  public String getObservation() {
	    return observation;
	  }
	  public void setRelevanceScore(int relevanceScore) {
	    this.relevanceScore = relevanceScore;
	  }
	  public int getRelevanceScore() {
	    return relevanceScore;
	  }
	  public void setDepthScore(double depthScore) {
	    this.depthScore = depthScore;
	  }
	  public double getDepthScore() {
	    return depthScore;
	  }
	  public void setCorrectnessScore(int correctnessScore) {
	    this.correctnessScore = correctnessScore;
	  }
	  public int getCorrectnessScore() {
	    return correctnessScore;
	  }
	  public void setEngagementScore(double engagementScore) {
	    this.engagementScore = engagementScore;
	  }
	  public double getEngagementScore() {
	    return engagementScore;
	  }
	  public void setOverallScore(int overallScore) {
	    this.overallScore = overallScore;
	  }
	  public int getOverallScore() {
	    return overallScore;
	  }
	public String getImprovementAreas() {
		return improvementAreas;
	}
	public void setImprovementAreas(String improvementAreas) {
		this.improvementAreas = improvementAreas;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	  
	  
}
