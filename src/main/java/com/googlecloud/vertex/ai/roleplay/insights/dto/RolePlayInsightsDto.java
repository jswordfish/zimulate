package com.googlecloud.vertex.ai.roleplay.insights.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RolePlayInsightsDto {
	
	
	Map<String, RoleplayInsightsDetail> mapCompetenciesInsights = new HashMap<>();
	
	Map<String, RoleplayInsightsDetail> mapVideoInsights = new HashMap<>();
	
	String overAllObservations = "NA";
	
	Integer overAllScoreInPercent =50;
	
	

	

	public Map<String, RoleplayInsightsDetail> getMapCompetenciesInsights() {
		return mapCompetenciesInsights;
	}





	public void setMapCompetenciesInsights(Map<String, RoleplayInsightsDetail> mapCompetenciesInsights) {
		this.mapCompetenciesInsights = mapCompetenciesInsights;
	}





	public Map<String, RoleplayInsightsDetail> getMapVideoInsights() {
		return mapVideoInsights;
	}





	public void setMapVideoInsights(Map<String, RoleplayInsightsDetail> mapVideoInsights) {
		this.mapVideoInsights = mapVideoInsights;
	}





	public String getOverAllObservations() {
		return overAllObservations;
	}





	public void setOverAllObservations(String overAllObservations) {
		this.overAllObservations = overAllObservations;
	}





	public Integer getOverAllScoreInPercent() {
		return overAllScoreInPercent;
	}





	public void setOverAllScoreInPercent(Integer overAllScoreInPercent) {
		this.overAllScoreInPercent = overAllScoreInPercent;
	}





	public static void main(String args[]) throws JsonProcessingException {
		RolePlayInsightsDto overall = new RolePlayInsightsDto();
		
		RoleplayInsightsDetail detail1 = new RoleplayInsightsDetail();
		detail1.setImprovementAreas("na");
		detail1.setScoreInPercent(50);
		detail1.setObservation("na");
		
		RoleplayInsightsDetail detail2 = new RoleplayInsightsDetail();
		detail2.setImprovementAreas("na");
		detail2.setScoreInPercent(22);
		detail2.setObservation("na");
		
		
		overall.getMapCompetenciesInsights().put("Sales Effectiveness", detail1);
		overall.getMapCompetenciesInsights().put("Cross Selling Effectiveness", detail2);
		overall.setOverAllObservations("na");
		overall.setOverAllScoreInPercent(28);
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(overall));
		
		
	}
}
