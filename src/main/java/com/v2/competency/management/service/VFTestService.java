package com.v2.competency.management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VFTest;

public interface VFTestService {
	
	public VFTest findByTestIdentifier( String testIdentifier, String companyId);
	
	public VFTest saveOrUpdate(VFTest test);
	
	public Page<VFTest> findTestsByCompanyId(String companyId, Pageable pageable);
	
	public Page<VFTest> findTestsContainingIdentifierText( String search, String companyId, Pageable pageable);

	public VFTest findByTestName(  String testName, String companyId);
	
	public Page<VFTest> findTestsContainingTestNameText(String search, String companyId, Pageable pageable);
}
