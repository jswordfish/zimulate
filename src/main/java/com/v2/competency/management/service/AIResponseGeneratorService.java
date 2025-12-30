package com.v2.competency.management.service;

import java.util.List;

import com.googlecloud.vertex.ai.communication.dto.ExpectedResponseCommunication;
import com.googlecloud.vertex.ai.dto.ExpectedResponse2;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.insights.dto.InsightsDto;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface AIResponseGeneratorService {
	
	public ExpectedResponse2 generateAnalysisUsingGemini(List<VFTestUserQuestionAnswer> answers, String testIdentifier, String fullName, String companyId);
	
	public ExpectedResponseCommunication generateAnalysisCommunicationUsingGemini(List<VFTestUserQuestionAnswer> answers, String testIdentifier, String fullName, String companyId);
	
	
	public InsightsDto generateAnalysisUsingGeminiForCompetencyInsights(List<VFTestUserQuestionAnswer> answers, String testIdentifier, String fullName, String companyId) throws RuntimeException ;
		
	
	public String fetchUniqueSkillsForTest(VFTest test) ;
	
	public String fetchUniqueSkillsForTestHashSeparated(VFTest test) ;
	
	public InsightForScenarioBasedQuestion generateAnalysisForScenarioBasedQuestion(VFTestUserQuestionAnswer answer);
	
	//public InsightForScenarioBasedQuestion generateAnalysisForScenarioBasedQuestion(VFTestUserQuestionAnswer answer);
	
	public String generateRolePlayAnalysisForRolePlayTest(String transcript);
	
	public String generateRolePlayAnalysisForRolePlayTestUsingAudioOrVideoLink(String audioOrVideoLink);
	
	public String generateAnalysisForScenarioBasedQuestionUsingAudioVideoLink(String question, MultipartFile audioOrVideoLink);
	
	
}
