package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.WorkflowNodeSession;

public interface WorkflowNodeSessionRepo extends CrudRepository<WorkflowNodeSession, Long> {
	
	@Query("select w from WorkflowNodeSession w where w.companyId=:companyId  and w.workflowId=:workflowId and w.workflowSessionId=:workflowSessionId and w.workflowNodeInstanceId=:workflowNodeInstanceId and w.email=:email and w.position=:position")
	public WorkflowNodeSession findUniqueWorkflowNodeSession( @Param("workflowId") Long workflowId, @Param("workflowSessionId") Long workflowSessionId,@Param("workflowNodeInstanceId") Long workflowNodeInstanceId,  @Param("email")  String email,@Param("position")  Integer position,   @Param("companyId")  String companyId);
	
	@Query("select w from WorkflowNodeSession w where w.companyId=:companyId and w.workflowId=:workflowId and w.workflowSessionId=:workflowSessionId and w.email=:email")
	public List<WorkflowNodeSession> findAllWorkflowNodesByCompanyId(@Param("companyId") String companyId, @Param("workflowId") Long workflowId, @Param("workflowSessionId") Long workflowSessionId, @Param("email")  String email);
	 
	@Query("select w from WorkflowNodeSession w where w.workflowSessionId=:workflowSessionId and w.recommendationsShown=true")
	public WorkflowNodeSession findShowRecommNodeForWorkflowSession(@Param("workflowSessionId") Long workflowSessionId);
	
}
