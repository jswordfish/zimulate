package com.v2.competency.management.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.WorkflowSession;

public interface WorkflowSessionRepo extends CrudRepository<WorkflowSession, Long> {
	
	@Query("select w from WorkflowSession w where w.companyId=:companyId  and w.workflow.id=:workFlowId and w.email=:email")
	public WorkflowSession findUniqueWorkflowSession( @Param("workFlowId") Long workFlowId, @Param("email")  String email,  @Param("companyId")  String companyId);
	
	@Query("select w from WorkflowSession w where w.companyId=:companyId")
	public Page<WorkflowSession> findAllWorkflowSessionByCompanyId(@Param("companyId") String companyId, Pageable pageable);

	 
	@Query("select w from WorkflowSession w where w.companyId=:companyId and w.email=:email")
	public Page<WorkflowSession> findAllWorkflowSessionForUserByCompanyId(@Param("companyId") String companyId, @Param("email")  String email, Pageable pageable);

}
