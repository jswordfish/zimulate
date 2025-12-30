package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.CompetencyAssessmentMapping;

public interface CompetencyAssessmentMappingService {
	
	public CompetencyAssessmentMapping findByBusinessKey(String competency, String parentCompetency, String testName, String companyId);
	
	 public CompetencyAssessmentMapping findByBusinessKeyWithoutTestName( String competency, String parentCompetency,  String companyId);
	 
	public List<CompetencyAssessmentMapping> findAssessmentsForCompetency(String competency,  String parentCompetency, String companyId);
	
	public CompetencyAssessmentMapping saveOrUpdate(CompetencyAssessmentMapping mapping);
	
	public List<CompetencyAssessmentMapping> findConsolidatedAssessmentForCompetenciesByGroupName(String consolidatedAssessmentGroupName,  String companyId);
	 
	public Page<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompany(  String companyId, Pageable pageable);
	
	public List<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompanyNoPagination(  String companyId);


}
