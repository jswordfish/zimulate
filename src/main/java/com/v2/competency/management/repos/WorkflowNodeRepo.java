package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.WorkflowNode;

public interface WorkflowNodeRepo extends CrudRepository<WorkflowNode, Long> {
	
	@Query("select w from WorkflowNode w where w.companyId=:companyId and w.position=:position and w.workFlowId=:workFlowId")
	public WorkflowNode findUniqueWorkflowNode(@Param("position") Integer position, @Param("workFlowId") Long workFlowId,  @Param("companyId")  String companyId);
	 
	 @Query("select w from WorkflowNode w where w.companyId=:companyId and w.workFlowId=:workFlowId  order by w.position")
	 List<WorkflowNode> findAllWorkflowNodes(@Param("companyId")  String companyId, @Param("workFlowId") Long workFlowId);
	 
	 
	 @Query("select w from WorkflowNode w where w.companyId=:companyId and w.workFlowId=:workFlowId and w.position >:position")
	 List<WorkflowNode> findAllWorkflowNodesAfterPosition(@Param("companyId")  String companyId, @Param("workFlowId") Long workFlowId, @Param("position") Integer position);
	 

}
