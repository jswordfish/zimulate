package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VideoAgent;

public interface VideoAgentRepo extends CrudRepository<VideoAgent, Long> {
	
	@Query("select v from VideoAgent v where v.name =:name and v.industry =:industry and v.agentType =:agentType and v.companyId =:companyId")
	public VideoAgent findByVideoAgentIdentifier( @Param("name") String name, @Param("industry") String industry,@Param("agentType") String agentType, @Param("companyId") String companyId);
	
	
	
	public Page<VideoAgent> findVideoAgentsByCompanyId(@Param("companyId") String companyId, Pageable pageable);
	
	@Query(value="SELECT i FROM VideoAgent i WHERE ( lower(i.name) LIKE lower(CONCAT('%',:search,'%'))  OR lower(i.industry) LIKE lower(CONCAT('%',:search,'%')) OR lower(i.agentType) LIKE lower(CONCAT('%',:search,'%')) OR lower(i.company) LIKE lower(CONCAT('%',:search,'%')) OR lower(i.products) LIKE lower(CONCAT('%',:search,'%'))) and i.companyId=:companyId")
	public Page<VideoAgent> searchVideoAgents(@Param("search") String search, @Param("companyId") String companyId, Pageable pageable);
	
	@Query(value="SELECT distinct i.industry FROM VideoAgent i WHERE i.companyId=:companyId")
	public List<String> findIndustries( @Param("companyId") String companyId);


}