package com.v2.competency.management.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WorkflowNodeDto {
	
	Integer position;
	
	String type = WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType();
	
	Boolean showResults;
	
	Boolean showTrainingRecommendations;
	
	Long workFlowId;
	
	Long videoAgentTrainingId;		
	
	Long rolePlayTrainingId;
	
	Long rolePlayAssessmentId;
	
	Long id;

}
