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
public class Overall {
	
	String analysisSummary;
	
	List<SkillCategory> skillGapsByCategory = new ArrayList<>();

}
