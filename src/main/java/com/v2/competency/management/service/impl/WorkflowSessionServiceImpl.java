package com.v2.competency.management.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.WorkflowSession;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.WorkflowSessionRepo;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.WorkflowSessionService;
import com.v2.competency.management.service.ZimulateWorkflowService;
@Service
@Transactional
public class WorkflowSessionServiceImpl implements WorkflowSessionService{
	
	@Autowired
	WorkflowSessionRepo repo;
	
	@Autowired
	ZimulateWorkflowService workflowService;
	
	@Autowired
	ZimulateWorkflowRepo workflowRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public WorkflowSession findUniqueWorkflowSession(Long workFlowId, String email, String companyId) {
		return repo.findUniqueWorkflowSession(workFlowId, email, companyId);
	}

	@Override
	public WorkflowSession saveOrUpdate(WorkflowSession session) {
		WorkflowSession session2 = findUniqueWorkflowSession(session.getWorkflow().getId(), session.getEmail(), session.getCompanyId());
		ZimulateWorkflow workflow = workflowRepo.findById(session.getWorkflow().getId()).get();
			if(session2 == null) {
				session.setCreateDate(new Date());
				session.setWorkflow(workflow);
				return repo.save(session);
			}
			
			session.setCreateDate(session2.getCreateDate());
			session.setId(session2.getId());
			session.setWorkflow(workflow);
			session.setUpdateDate(new Date());
			mapper.map(session, session2);
		return repo.save(session2);
	}

	@Override
	public Page<WorkflowSession> findAllWorkflowSessionByCompanyId(String companyId, Pageable pageable) {
		return repo.findAllWorkflowSessionByCompanyId(companyId, pageable);
	}

	@Override
	public Page<WorkflowSession> findAllWorkflowSessionForUserByCompanyId(String companyId, String email,
			Pageable pageable) {
		return repo.findAllWorkflowSessionForUserByCompanyId(companyId, email, pageable);
	}

}
