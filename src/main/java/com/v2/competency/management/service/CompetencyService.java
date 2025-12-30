package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Competency;

public interface CompetencyService {

	
	public List<Competency> findByLevelAndCompanyId(String level, String companyId);
	 
	 
	 public Competency findByCompetency(  String competency, String companyId);
	 
	 
	 public List<Competency> findByParentCompetency( String competency, String companyId);
	 
	 public Competency saveOrUpdate(Competency competency);
	 
	 public Page<Competency> getCompetenciesByCompanyId(String companyId, Pageable pageable);
	 
	 
	 public Page<Competency> getCompetenciesByLevelAndCompanyId( String level,String companyId, Pageable pageable);
	 
	 public Competency findByCompetencyAndParentCompetency( String competency, String parentCompetency, String companyId);
}
