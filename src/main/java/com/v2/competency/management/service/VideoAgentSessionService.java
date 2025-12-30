package com.v2.competency.management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.VideoAgentSession;

public interface VideoAgentSessionService {
	
public VideoAgentSession findUniqueVideoAgentSession( Long videoAgentId,  String email,   String companyId, Integer attempt);
	
	public VideoAgentSession createSession(VideoAgentSession session);
	
	public Page<VideoAgentSession> findAllVideoAgentSessionByCompanyId(String companyId, Pageable pageable);
	 
	public Page<VideoAgentSession> findAllVideoAgentSessionForUserByCompanyId( String companyId,   String email, Pageable pageable);
	
	//public Page<VideoAgentSession> findAllVideoAgentSessionForUserForWorkflowByCompanyId(String companyId,   String email,  Long workFlowId, Pageable pageable);




}
