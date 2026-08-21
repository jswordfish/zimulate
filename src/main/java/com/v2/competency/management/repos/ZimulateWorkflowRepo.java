package com.v2.competency.management.repos;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.WorkFlowDto;
import com.v2.competency.management.entities.ZimulateWorkflow;

public interface ZimulateWorkflowRepo extends CrudRepository<ZimulateWorkflow, Long> {
	
	@Query("select w from ZimulateWorkflow w where w.companyId=:companyId and w.name=:name and w.industry=:industry")
	public ZimulateWorkflow findUniqueZimulateWorkflow(@Param("name") String name, @Param("industry") String industry, @Param("companyId")  String companyId);
	 
	 @Query("select w from ZimulateWorkflow w where w.companyId=:companyId")
	 Page<ZimulateWorkflow> findAllWorkflows(@Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 @Query("SELECT w FROM ZimulateWorkflow w " +
		       "WHERE w.companyId = :companyId " +
		       "AND (" +
		       ":search IS NULL OR :search = '' OR " +
		       "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(w.objective) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(w.industry) LIKE LOWER(CONCAT('%', :search, '%'))" +
		       ")")
		Page<ZimulateWorkflow> findAllWorkflows(
		        @Param("companyId") String companyId,
		        @Param("search") String search,
		        Pageable pageable);
	 
	 @Query(
		        value = "SELECT new com.v2.competency.management.dtos.WorkFlowDto(" +
		                "w.name, " +
		                "w.objective, " +
		                "w.industry, " +
		                "w.id, " +
		                "w.companyId, " +
		                "w.complete, " +
		                "w.navigationBack, " +

		                "CASE " +
		                "    WHEN w.complete IS NULL OR w.complete = false " +
		                "    THEN true " +
		                "    ELSE false " +
		                "END, " +

		                "CASE " +
		                "    WHEN NOT EXISTS (" +
		                "        SELECT ws.id " +
		                "        FROM WorkflowSession ws " +
		                "        WHERE ws.workflow.id = w.id " +
		                "        AND ws.companyId = w.companyId " +
		                "    ) " +
		                "    THEN true " +
		                "    ELSE false " +
		                "END" +

		                ") " +

		                "FROM ZimulateWorkflow w " +

		                "WHERE w.companyId = :companyId " +

		                "AND (" +
		                "    :search IS NULL OR :search = '' OR " +
		                "    LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		                "    LOWER(w.objective) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		                "    LOWER(w.industry) LIKE LOWER(CONCAT('%', :search, '%'))" +
		                ")",

		        countQuery = "SELECT COUNT(w) " +
		                     "FROM ZimulateWorkflow w " +
		                     "WHERE w.companyId = :companyId " +
		                     "AND (" +
		                     "    :search IS NULL OR :search = '' OR " +
		                     "    LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		                     "    LOWER(w.objective) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		                     "    LOWER(w.industry) LIKE LOWER(CONCAT('%', :search, '%'))" +
		                     ")"
		)
		Page<WorkFlowDto> findAllWorkflowsDto(
		        @Param("companyId") String companyId,
		        @Param("search") String search,
		        Pageable pageable
		);
	 
	 Optional<ZimulateWorkflow> findByIdAndCompanyId(
	            Long id,
	            String companyId
	    );
	 

}
