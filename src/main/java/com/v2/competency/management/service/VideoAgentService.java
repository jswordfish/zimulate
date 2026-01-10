package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.VideoAgent;

public interface VideoAgentService {
	
	public VideoAgent findByVideoAgentIdentifier( String name,  String industry, String agentType, String companyId);	
	
	public VideoAgent saveOrUpdate(VideoAgent agent);
	
	public Page<VideoAgent> findVideoAgentsByCompanyId( String companyId, Pageable pageable);
	
	public Page<VideoAgent> searchVideoAgents(String search,  String companyId, Pageable pageable);
	
	public List<String> findIndustries(String companyId);
	
	
	
}
