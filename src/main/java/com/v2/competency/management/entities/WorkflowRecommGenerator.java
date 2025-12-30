package com.v2.competency.management.entities;

import java.io.IOException;

public interface WorkflowRecommGenerator {
	
	
	public boolean checkIfRecommCanBeGenerated(Long rolePlayAssessmentId, Long workflowSessionId);
	
	public String generateRecommendations(Long workflowSessionId) throws IOException;

}
