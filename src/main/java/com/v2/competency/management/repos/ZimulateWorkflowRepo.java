package com.v2.competency.management.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.ZimulateWorkflow;

public interface ZimulateWorkflowRepo extends CrudRepository<ZimulateWorkflow, Long> {
	
	@Query("select w from ZimulateWorkflow w where w.companyId=:companyId and w.name=:name and w.industry=:industry")
	public ZimulateWorkflow findUniqueZimulateWorkflow(@Param("name") String name, @Param("industry") String industry, @Param("companyId")  String companyId);
	 
	 @Query("select w from ZimulateWorkflow w where w.companyId=:companyId")
	 Page<ZimulateWorkflow> findAllWorkflows(@Param("companyId")  String companyId, Pageable pageable);
	 

}
