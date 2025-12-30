package com.v2.competency.management.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.v2.competency.management.dtos.WorkflowNodeType;

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
public class WorkflowNode extends Base {
	
	Integer position;
	
	String type = WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType();
	
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "video_agent_id", nullable = true) 
	VideoAgent videoAgentTraining;
	
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "role_play_test_training_id", nullable = true) 
	VFRolePlayTest rolePlayTraining;
	
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "role_play_test_assessment_id", nullable = true) 
	VFRolePlayTest rolePlayAssessment;
	
	Boolean showResults;
	
	Boolean showTrainingRecommendations;
	
	@NotNull
	Long workFlowId;
	
	@Transient
	Long videoAgentTrainingId;		
	
	@Transient
	Long rolePlayTrainingId;
	
	@Transient
	Long rolePlayAssessmentId;
	
	String image;

}
