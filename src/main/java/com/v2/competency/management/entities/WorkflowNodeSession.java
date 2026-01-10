package com.v2.competency.management.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNodeSession extends Base{
	
	Long workflowSessionId;// work flow session
	
	Long workflowId;
	
	Long workflowNodeId; //Workflow node id not the node instance/session id
	
	Long workflowNodeInstanceId;  // node session id.. if its VideoAgent, then value will be VideoAgent Session id
	
	String nodeType;
	
	Integer position;
	
	String email;
	
	@OneToOne
	@JoinColumn(name = "video_agent_session_id", nullable = true) 
	VideoAgentSession videoAgentSession;
	
	@OneToOne
	@JoinColumn(name = "role_play_training_session_id", nullable = true) 
	VFRolePlayTestSession rolePlayTrainingSession;
	
	
	@OneToOne
	@JoinColumn(name = "role_play_assessment_session_id", nullable = true) 
	VFRolePlayTestSession rolePlayAssessmentSession;
	
	
	Boolean resultsShown;
	
	Boolean recommendationsShown;
	
	
}
