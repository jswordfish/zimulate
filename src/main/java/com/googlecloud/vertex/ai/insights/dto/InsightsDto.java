package com.googlecloud.vertex.ai.insights.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InsightsDto {
	
	Map<String, Object> parameters = new HashMap<>();
	
	OverallInsights insights;

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public OverallInsights getInsights() {
		if(this.parameters == null || this.parameters.size() == 0) {
			return null;
		}
		
		OverallInsights insights = OverallInsights.builder().build();
		insights.setOverAllScore( Float.parseFloat(parameters.get("Overall Score").toString()) );
		insights.setOverAllInsights(parameters.get("Over All Insights").toString());
		Set<String> params = fetchParameters(parameters);
		List<ParameterInsight> list = new ArrayList<>();
			for(String param : params) {
				ParameterInsight insight = ParameterInsight.builder().name(param)
											.score(Float.parseFloat( (parameters.get("Score for "+param)==null?0:(parameters.get("Score for "+param))).toString()))
											.insight((parameters.get("Insights for "+param)==null?"":(parameters.get("Insights for "+param))).toString())
											.build();
				list.add(insight);
			}
		insights.setList(list);	
		return insights;
	}

	
	private Set<String> fetchParameters(Map<String, Object> parameters){
		 Set<String> params = new HashSet<>();
		 for(String key : parameters.keySet()) {
			 if(key.startsWith("Score for ")) {
				 params.add(key.substring(10, key.length()));
			 }
		 }
		 return params;
	}
	
}
