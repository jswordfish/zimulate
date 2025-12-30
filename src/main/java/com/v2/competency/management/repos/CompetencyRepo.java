package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Competency;

public interface CompetencyRepo extends CrudRepository<Competency, Long>  {
	
	
	public List<Competency> findByLevelAndCompanyId(String level, String companyId);
	 
	 
	 @Query("select o from Competency o where o.competency =:competency and o.companyId =:companyId")
	 public Competency findByCompetency(@Param("competency")  String competency, String companyId);
	 
	 @Query("select o from Competency o where o.competency =:competency and o.parentCompetency =:parentCompetency  and o.companyId =:companyId")
	 public Competency findByCompetencyAndParentCompetency(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency, String companyId);
	 
	 
	 @Query("select o from Competency o where o.parentCompetency =:parentCompetency and o.companyId =:companyId")
	 public List<Competency> findByParentCompetency(@Param("parentCompetency")  String parentCompetency, String companyId);
	 
	 public Page<Competency> getCompetenciesByCompanyId(@Param("companyId") String companyId, Pageable pageable);
	 
	 
	 public Page<Competency> getCompetenciesByLevelAndCompanyId(@Param("level") String level,@Param("companyId") String companyId, Pageable pageable);
}
