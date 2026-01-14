package com.v2.competency.management.entities;

import javax.persistence.Entity;
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
public class WorkflowSession extends Base{
	
	String email;
	
	@OneToOne
	ZimulateWorkflow workflow;
	
	String status = WorkflowSessionStatus.NOT_STARTED.getStatus();
	
	/**
	 * Overall json as we get from Gemini
	 */
	String recommendations;
	
	/**
	 * A comma separated Video agent ids
	 */
	String dynamicTrainingAgentIds;
	
	Integer positon;

}
