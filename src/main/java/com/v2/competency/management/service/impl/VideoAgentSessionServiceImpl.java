package com.v2.competency.management.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.entities.VideoAgentSession;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.repos.VideoAgentSessionRepo;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.VideoAgentSessionService;
import com.v2.competency.management.service.ZimulateWorkflowService;

@Service
@Transactional
public class VideoAgentSessionServiceImpl implements VideoAgentSessionService{
	
	@Autowired
	VideoAgentSessionRepo repo;
	
	@Autowired
	ZimulateWorkflowService workflowService;
	
	@Autowired
	ZimulateWorkflowRepo workflowRepo;
	
	@Autowired
	VideoAgentRepo videoAgentRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public VideoAgentSession findUniqueVideoAgentSession( Long videoAgentId, String email, String companyId, Integer count) {
		return repo.findUniqueVideoAgentSession( videoAgentId, email, companyId, count);
	}

	@Override
	public VideoAgentSession createSession(VideoAgentSession session) {
		Integer count = repo.findNumberOfAttempts(session.getVideoAgent().getId(), session.getEmail(), session.getCompanyId());
		
		VideoAgent videoAgent = videoAgentRepo.findById(session.getVideoAgent().getId()).get();
		session.setCreateDate(new Date());
		session.setVideoAgent(videoAgent);
		session.setAttempt((++count));
		return repo.save(session);
	}

	@Override
	public Page<VideoAgentSession> findAllVideoAgentSessionByCompanyId(String companyId, Pageable pageable) {
		return repo.findAllVideoAgentSessionByCompanyId(companyId, pageable);
	}

	@Override
	public Page<VideoAgentSession> findAllVideoAgentSessionForUserByCompanyId(String companyId, String email,
			Pageable pageable) {
		return repo.findAllVideoAgentSessionForUserByCompanyId(companyId, email, pageable);
	}

	

}
