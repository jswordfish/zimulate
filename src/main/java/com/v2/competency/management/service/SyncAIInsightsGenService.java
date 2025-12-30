package com.v2.competency.management.service;

import java.util.List;
import java.util.Map;

import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public interface SyncAIInsightsGenService {

	
	public List<UserCompetencyWiseScoreForAssessment> generateInsightsForScenarioBasedQsInSync(String testName, Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs, String testIdentifier, String email, String companyId, Integer attempt);

	
	public void generateInsightsForRolePlayBasedAssessment(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String transcript);
	
	public void generateInsightsForScenarioBasedAnswer(Long answerId);
	
	public void generateInsightsForRolePlayBasedAssessmentSync(String testName, String email, String companyId,
			VFRolePlayTestSession rolePlayTestSession, String transcript) ;
	
	public void generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String transcript, String mergedVideoPath);
	
}
