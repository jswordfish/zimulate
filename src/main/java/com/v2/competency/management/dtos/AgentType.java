package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AgentType {
	
	SALES("SALES"), SUPPORT("SUPPORT"), TRAINER("TRAINER"), EVALUATOR("EVALUATOR"),
	PRODUCT_ONBOARDING("PRODUCT ONBOARDING"), EMPLOYEE_ONBOARDING("EMPLOYEE ONBOARDING"), OTHER("OTHER");
	
	String type;
	
	private AgentType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	
	
	public List<String> getAllAgents(){
		return Arrays.stream(AgentType.values())
				.map(val -> val.getType())
				.collect(Collectors.toList());
	}
}
