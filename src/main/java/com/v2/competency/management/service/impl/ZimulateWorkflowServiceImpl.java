package com.v2.competency.management.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.ZimulateWorkflowService;
@Service
@Transactional
public class ZimulateWorkflowServiceImpl implements ZimulateWorkflowService{
	
	@Autowired
	ZimulateWorkflowRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public ZimulateWorkflow findUniqueZimulateWorkflow(String name, String industry, String companyId) {
		return repo.findUniqueZimulateWorkflow(name, industry, companyId);
	}

	@Override
	public ZimulateWorkflow saveOrUpdate(ZimulateWorkflow workflow) {
		ZimulateWorkflow  workflow2 = findUniqueZimulateWorkflow(workflow.getName(), workflow.getIndustry(), workflow.getCompanyId());
			if(workflow2 == null) {
				workflow.setCreateDate(new Date());
				return repo.save(workflow);
			}
			
			workflow.setId(workflow2.getId());
			workflow.setUpdateDate(new Date());
			workflow.setCreateDate(workflow2.getCreateDate());
			mapper.map(workflow, workflow2);
		return repo.save(workflow2);
	}

	@Override
	public Page<ZimulateWorkflow> findAllWorkflows(String companyId, Pageable pageable) {
		return repo.findAllWorkflows(companyId, pageable);
	}

}
