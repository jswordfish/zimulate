package com.googlecloud.vertex.ai.roleplay.insights.dto;

public class RoleplayInsightsDetail {
	
	String label;
	
	String observation = "NA";
	
	String improvementAreas = "NA";
	
	Integer scoreInPercent = 20;

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getImprovementAreas() {
		return improvementAreas;
	}

	public void setImprovementAreas(String improvementAreas) {
		this.improvementAreas = improvementAreas;
	}

	public Integer getScoreInPercent() {
		return scoreInPercent;
	}

	public void setScoreInPercent(Integer scoreInPercent) {
		this.scoreInPercent = scoreInPercent;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	

}
