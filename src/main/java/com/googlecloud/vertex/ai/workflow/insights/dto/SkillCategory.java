package com.googlecloud.vertex.ai.workflow.insights.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCategory {
	
	String category;
	
	String reasoning;
	
	List<LearningInsightsDetail> skillGapDetails = new ArrayList<>();

}
