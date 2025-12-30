package com.googlecloud.vertex.ai.communication.dto;

public class Content {

	
	  private int conversationIdeasScore;
	  private int overallScore;
	  
	  String improvementAreas;

	public int getConversationIdeasScore() {
		return conversationIdeasScore;
	}

	public void setConversationIdeasScore(int conversationIdeasScore) {
		this.conversationIdeasScore = conversationIdeasScore;
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
