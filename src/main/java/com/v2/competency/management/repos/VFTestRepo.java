package com.v2.competency.management.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VFTest;

public interface VFTestRepo extends CrudRepository<VFTest, Long> {
	
	@Query("select v from VFTest v where v.testIdentifier =:testIdentifier and v.companyId =:companyId")
	public VFTest findByTestIdentifier( @Param("testIdentifier") String testIdentifier, @Param("companyId") String companyId);
	
	@Query("select v from VFTest v where v.testName =:testName and v.companyId =:companyId")
	public VFTest findByTestName( @Param("testName") String testName, @Param("companyId") String companyId);
	

	
	public Page<VFTest> findTestsByCompanyId(@Param("companyId") String companyId, Pageable pageable);
	
	@Query(value="SELECT i FROM VFTest i WHERE lower(i.testIdentifier) LIKE lower(CONCAT('%',:search,'%')) and i.companyId=:companyId")
	public Page<VFTest> findTestsContainingIdentifierText(@Param("search") String search, @Param("companyId") String companyId, Pageable pageable);
	
	@Query(value="SELECT i FROM VFTest i WHERE lower(i.testName) LIKE lower(CONCAT('%',:search,'%')) and i.companyId=:companyId")
	public Page<VFTest> findTestsContainingTestNameText(@Param("search") String search, @Param("companyId") String companyId, Pageable pageable);


}
