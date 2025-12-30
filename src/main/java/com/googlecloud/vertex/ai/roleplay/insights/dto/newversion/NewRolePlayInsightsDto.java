package com.googlecloud.vertex.ai.roleplay.insights.dto.newversion;

import java.util.ArrayList;
import java.util.List;

public class NewRolePlayInsightsDto {
	
	List<SectionResult> sections = new ArrayList<>();
	
	String overAllObservation;
	
	Integer overAllScore;

	public List<SectionResult> getSections() {
		return sections;
	}

	public void setSections(List<SectionResult> sections) {
		this.sections = sections;
	}

	public String getOverAllObservation() {
		return overAllObservation;
	}

	public void setOverAllObservation(String overAllObservation) {
		this.overAllObservation = overAllObservation;
	}

	public Integer getOverAllScore() {
		return overAllScore;
	}

	public void setOverAllScore(Integer overAllScore) {
		this.overAllScore = overAllScore;
	}
	
	

}


