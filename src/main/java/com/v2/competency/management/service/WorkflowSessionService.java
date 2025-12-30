package com.v2.competency.management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.WorkflowSession;

public interface WorkflowSessionService {
	
	public WorkflowSession findUniqueWorkflowSession(  Long workFlowId,   String email,   String companyId);
	
	public WorkflowSession saveOrUpdate(WorkflowSession session);
	
	public Page<WorkflowSession> findAllWorkflowSessionByCompanyId(String companyId, Pageable pageable);
	 
	public Page<WorkflowSession> findAllWorkflowSessionForUserByCompanyId( String companyId,   String email, Pageable pageable);


}
