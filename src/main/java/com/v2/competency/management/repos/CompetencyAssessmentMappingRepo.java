package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.CompetencyAssessmentMapping;

public interface CompetencyAssessmentMappingRepo extends CrudRepository<CompetencyAssessmentMapping, Long> {
	
	

	 @Query("select o from CompetencyAssessmentMapping o where o.competency =:competency and o.companyId =:companyId and o.parentCompetency=:parentCompetency and o.testName=:testName")
	 public CompetencyAssessmentMapping findByBusinessKey(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency, @Param("testName")  String testName, String companyId);
	 
	 @Query("select o from CompetencyAssessmentMapping o where o.competency =:competency and o.companyId =:companyId and o.parentCompetency=:parentCompetency")
	 public CompetencyAssessmentMapping findByBusinessKeyWithoutTestName(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,  String companyId);
	 
	 @Query("select o from CompetencyAssessmentMapping o where o.competency =:competency and o.parentCompetency =:parentCompetency  and o.companyId =:companyId and (o.consolidatedAssessments is null or o.consolidatedAssessments=false)")
	 public List<CompetencyAssessmentMapping> findAssessmentsForCompetency(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency, String companyId);
	 
	 
	 @Query("select o from CompetencyAssessmentMapping o where o.consolidatedAssessmentGroupName =:consolidatedAssessmentGroupName and  o.companyId =:companyId and (o.consolidatedAssessments=true)")
	 public List<CompetencyAssessmentMapping> findConsolidatedAssessmentForCompetenciesByGroupName(@Param("consolidatedAssessmentGroupName")  String consolidatedAssessmentGroupName,  String companyId);
	 
	 @Query("select o from CompetencyAssessmentMapping o where o.companyId =:companyId and (o.consolidatedAssessments=true)")
	 public Page<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompany(  String companyId,  Pageable pageable);
	 
	 @Query("select o from CompetencyAssessmentMapping o where o.companyId =:companyId and (o.consolidatedAssessments=true)")
	 public List<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompanyNoPagination(  String companyId);
	 
}
