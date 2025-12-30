package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.WorkflowNodeSession;

public interface WorkflowNodeSessionService {
	
	public WorkflowNodeSession findUniqueWorkflowNodeSession( Long workFlowId, Long workFlowSessionId, Long workFlowNodeInstanceId, String email,  Integer position,  String companyId);
	
	public List<WorkflowNodeSession> findAllWorkflowNodesByCompanyId(String companyId,  Long workFlowId, Long workFlowSessionId, String email);
	 
	public WorkflowNodeSession markComplete(WorkflowNodeSession node);
	
	public WorkflowNodeSession findById(Long id);
	
	public WorkflowNodeSession findShowRecommNodeForWorkflowSession(Long workflowSessionId);
	

}
