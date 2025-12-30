package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.VFRolePlayTest;

public interface VFRolePlayTestService {
	
	
	//public VFRolePlayTest findUniqueRecord( String testName, String competency, String parentCompetency, String questionText, String companyId);
	public VFRolePlayTest findUniqueRecord( String testName, String companyId);
	
	public List<VFRolePlayTest> findRolePlayTestsForCompetency(String competency, String parentCompetency, String companyId);
	
	public VFRolePlayTest findRolePlayTestsByTestName(String companyId, String testName);
	 
	 public Page<VFRolePlayTest> getRolePlayTestsByCompanyId(String companyId, Pageable pageable);
	 
	 public VFRolePlayTest saveOrUpdate(VFRolePlayTest test);
	 
	 public List<VFRolePlayTest> findPublishedTests(String companyId);
	 
	 PaginatedResponseDto getAllRolePlayTests(int page);
	 
	 public PaginatedResponseDto getRolePlayTestsByCompanyAndIndustry(String companyId, String industries, int page);
	 
	 PaginatedResponseDto getRolePlayTestsFiltered(String companyId, String industries, String search, String sort, int page, int size);
	 
	 PaginatedResponseDto getAllIndustriesPaginated(String companyId, int page);
	 
	 public Page<VFRolePlayTest> searchTrainingRolePlays( String companyId,  Pageable pageable);
	 
	 public Page<VFRolePlayTest> searchAssessmentRolePlays( String companyId,   Pageable pageable);
	 
}
