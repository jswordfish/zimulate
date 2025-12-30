package com.v2.competency.management.service;

public interface AgentService {
	
	public String createOrUpdateKnowledgeBase(java.io.File file, String knowledBaseName);
	
	public String createOrUpdateAgent(String agentName, String agentPrompt, String knowledgeBaseId);
	
	

}
