package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.JobDescription;

public interface JobDescRepo extends CrudRepository<JobDescription, Long> {

	
	 public JobDescription findByJobDescNameAndCompanyId(String jobDescName, String companyId);
	 
	 @Query("select r from JobDescription r where r.companyId=:companyId")
	 List<JobDescription> findJobDescriptionsForCompany(@Param("companyId")  String companyId);
	 
	 public Page<JobDescription> findJobDescriptionsByCompanyId(@Param("companyId") String companyId, Pageable pageable);

}


