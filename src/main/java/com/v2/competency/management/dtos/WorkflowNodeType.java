package com.v2.competency.management.dtos;

public enum WorkflowNodeType {
	
	VIDEO_AGENT_TRAINING("VIDEO AGENT TRAINING"), 
	ROLEPLAY_TRAINING("ROLEPLAY TRAINING"), 
	ROLPLAY_ASSESSMENT("ROLEPLAY ASSESSMENT"),
	SHOW_RESULTS("SHOW RESULTS"),
	SHOW_TRAINING_RECOMMENDATIONS("SHOW TRAINING RECOMMENDATIONS");
	
	String workflowNodeType;
	
	
	private WorkflowNodeType(String workflowNodeType) {
		this.workflowNodeType = workflowNodeType;
	}

	

	public String getWorkflowNodeType() {
		return workflowNodeType;
	}
	

}
