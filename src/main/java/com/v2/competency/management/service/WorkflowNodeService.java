package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.WorkflowNode;

public interface WorkflowNodeService {
	
	public WorkflowNode findUniqueWorkflowNode( Integer position,Long workFlowId,   String companyId);
	
	public WorkflowNode saveOrUpdate(WorkflowNode workflowNode);
	 
	 List<WorkflowNode> findAllWorkflowNodes( String companyId, Long workFlowId);
	 

}
