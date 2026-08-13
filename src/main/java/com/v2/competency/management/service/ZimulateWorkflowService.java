package com.v2.competency.management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.ZimulateWorkflow;

public interface ZimulateWorkflowService {
	
	public ZimulateWorkflow findUniqueZimulateWorkflow(String name, String industry, String companyId);
	
	public ZimulateWorkflow saveOrUpdate(ZimulateWorkflow workflow);
	 
	 Page<ZimulateWorkflow> findAllWorkflows(String companyId, Pageable pageable);
	 
	 public PaginatedResponseDto findAllWorkflows(
		        String companyId,
		        String search,
		        int page,
		        int size);

}
