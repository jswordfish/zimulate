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
	
	@Query("SELECT i FROM VideoAgent i " +
		       "WHERE i.companyId = :companyId " +
		       "AND (:search IS NULL OR TRIM(:search) = '' " +
		       "OR LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.objective) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.url) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.industry) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.agentType) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.kbId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.openingStatement) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.prompt) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.videoAgentForCompany) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.products) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.company) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(i.image) LIKE LOWER(CONCAT('%', :search, '%')))")
	public Page<VideoAgent> searchVideoAgents(@Param("search") String search, @Param("companyId") String companyId, Pageable pageable);
	
	@Query(value="SELECT distinct i.industry FROM VideoAgent i WHERE i.companyId=:companyId")
	public List<String> findIndustries( @Param("companyId") String companyId);


}