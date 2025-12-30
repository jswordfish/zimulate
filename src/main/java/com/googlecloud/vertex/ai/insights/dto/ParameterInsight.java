package com.googlecloud.vertex.ai.insights.dto;

import java.util.List;

import com.v2.competency.management.dtos.AssessmentTraversalPath;
import com.v2.competency.management.dtos.Path;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParameterInsight {
	
	String name;
	
	Float score;
	
	String insight;

}
