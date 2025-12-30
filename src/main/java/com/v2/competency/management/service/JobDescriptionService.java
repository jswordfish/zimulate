package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.JobDescription;

public interface JobDescriptionService {
	
	public JobDescription findByJobDescriptionAndCompanyId(String jobDescName, String companyId);
	 
	 List<JobDescription> findJobDescriptionsForCompany( String companyId);
	 
	 public Page<JobDescription> findJobDescriptionsByCompanyId(String companyId, Pageable pageable);
	 
	 
	 public JobDescription saveOrUpdate(JobDescription role);

}
