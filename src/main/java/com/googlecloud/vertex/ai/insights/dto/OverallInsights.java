package com.googlecloud.vertex.ai.insights.dto;

import java.util.List;

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
public class OverallInsights {

	
	Float overAllScore;
	
	String overAllInsights;
	
	List<ParameterInsight> list;
}
