package com.v2.competency.management.service;

import java.util.List;
import java.util.Map;

import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public interface ASyncAIInsightsGenService {
	
	
	public void generateInsightsForScenarioBasedQsInAsync(String testName, Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs, String testIdentifier, String email, String companyId, Integer attempt);
	
	
	public void generateInsightsForRolePlayBasedAssessmentInAsync(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String transcript);
	
	
	public void generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String transcript, String mergedVideoPath);
	
	public void generateInsightsForRolePlayBasedAssessmentWithUnifiedPromptInAsyncDJ(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String mergedVideoPath);
	
	public void submitGoogleFullVideoForAnalysis(
			String persona,
		    String email,
		    String firstName,
		    String lastName,
		    String testName,
		    Integer attempt,
		    String companyId,
		    String googleBucketPath, 
		    String location,
		    String model, String videoLink, Long workflowSessionId);
	
	public void submitGoogleFullVideoForAnalysisSync(
			String persona,
		    String email,
		    String firstName,
		    String lastName,
		    String testName,
		    Integer attempt,
		    String companyId,
		    String googleBucketPath, 
		    String location,
		    String model, String videoLink, Long workflowSessionId);
	
	
}
