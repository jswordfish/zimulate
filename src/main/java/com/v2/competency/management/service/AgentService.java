package com.v2.competency.management.service;

import com.v2.competency.management.elevanlabs.dtos.Root;

public interface AgentService {
	
	public String createOrUpdateKnowledgeBase(java.io.File file, String knowledBaseName);
	
	public String createOrUpdateAgent(String agentName, String agentPrompt, String knowledgeBaseId);
	
	public String createOrUpdateAgent(Root root, String agentId);

}
