package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.UserTestSessionComputeScoreDto;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;

@RestController
public class AI_Platform_UserWebService {
	
	@Autowired
	VFTestUserSessionService sessionService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService competencyWiseScoreForAssessmentService;
	
	@Autowired
	VFTestUserQuestionAnswerService answerService;
	
	@Autowired
	VFTestService testService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	 XmlMapper xmlMapper = new XmlMapper();
	
	@RequestMapping(value="mcqScenarioAssessmentsHistoryForUser",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> mcqScenarioAssessmentsHistoryForUser( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String email, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	// Page<VFTestUserSession> sessions =sessionService.findUserSessionsForTest(testIdentifier, companyId, PageRequest.of(pageNumber, 15));
	 Page<VFTestUserSession> sessions =sessionService.findUserSessionsByEmail(email, companyId, PageRequest.of(pageNumber, 15));
	 List<VFTestUserSession> list = sessions.getContent();
	 	for(VFTestUserSession sess : list) {
	 		VFTest test =  testService.findByTestIdentifier(sess.getTestIdentifier() ,companyId);
	 		String xml = test.getTestXmlForKB();
	 		CompetencyTest compTest =  xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
	 		List<CompetencyDto> dtos =  compTest.getKbCompetencies();
	 		Set<String> skillsAssociatedWithTest = new HashSet<>();
	 		for(CompetencyDto dto : dtos){
	 			String skill = dto.getCompetency()+" IN "+dto.getParentCompetency();
	 			skillsAssociatedWithTest.add(skill);
	 		}
	 		sess.setSkillsAssociatedWithTest(skillsAssociatedWithTest);
	 		
	 	}
	 		

	 	 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(sessions.getNumber());
		 res.setRecordsTo(sessions.getNumberOfElements());
		 res.setTotalNumberOfPages(sessions.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="computeScores",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> computeScores( @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		/**
		 * Step 1 Get the scores computed for user - competencies 
		 */
		
		int page = 0;
		Page<UserCompetencyWiseScoreForAssessment> res =  competencyWiseScoreForAssessmentService.findRecordsByCompanyIdAndquestionType(Question_Type.SUBJECTIVE.getType(), companyId, PageRequest.of(page, 50));
		compute(res.getContent());
		while(res.hasNext()) {
			page++;
			res =  competencyWiseScoreForAssessmentService.findRecordsByCompanyIdAndquestionType(Question_Type.SUBJECTIVE.getType(), companyId, PageRequest.of(page, 50));
			compute(res.getContent());
		}
		
		/**
		 * Step 2 Get the scores calculated for user assessments
		 */
		
		List<UserTestSessionComputeScoreDto> testScores =  competencyWiseScoreForAssessmentService.findTestScores(companyId);
		for(UserTestSessionComputeScoreDto dto : testScores) {
		VFTestUserSession sess = 	sessionService.finfVFTestUserSessionByEmail(dto.getEmail(), companyId, dto.getTestIdentifier(), dto.getAttempt());
			sess.setFinalScore(dto.getAverageScore().floatValue());
			sessionService.saveOrUpdate(sess);
		}
		
	
	 return ResponseEntity.ok("OK");
	}
	
	private void compute(List<UserCompetencyWiseScoreForAssessment> list) {
		try {
			for(UserCompetencyWiseScoreForAssessment comp : list) {
				
				if(comp.getAiScore() == null) {
					Float totalScore = 0f;
					Integer totalsQs = 0;
					String ids = comp.getAnswerIds();
					String idsArray[] = ids.split(",");
					for(String id : idsArray) {
						if(id != null && id.trim().length() != 0) {
							Long ansId = Long.parseLong(id);
							VFTestUserQuestionAnswer ans = answerService.getById(ansId);
							
							String aiAnalysisJson = ans.getAiAnalysisJson();
							if(aiAnalysisJson != null) {
								InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = objectMapper.readValue(aiAnalysisJson, InsightForScenarioBasedQuestion.class);
								totalsQs++;
								totalScore += insightForScenarioBasedQuestion.getOverAllScoreInPErcentage();
							}
							
							
							
							
						}
					}
					if(totalsQs !=0) {
						Float averageCompWiseScore = totalScore / totalsQs;
						comp.setAiScore(averageCompWiseScore);
						//save comp
						competencyWiseScoreForAssessmentService.updateAIScore(comp.getId(), averageCompWiseScore);
					}
					
				}
				
				if(comp.getReviewerScore() == null) {
					Float totalScore = 0f;
					Integer totalsQs = 0;
					String ids = comp.getAnswerIds();
					String idsArray[] = ids.split(",");
					for(String id : idsArray) {
						if(id != null && id.trim().length() != 0) {
							Long ansId = Long.parseLong(id);
							VFTestUserQuestionAnswer ans = answerService.getById(ansId);
							
							String reviewerAnalysisJson = ans.getReviewerAnalysisJson();
							if(reviewerAnalysisJson != null) {
								InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = objectMapper.readValue(reviewerAnalysisJson, InsightForScenarioBasedQuestion.class);
								totalsQs++;
								totalScore += insightForScenarioBasedQuestion.getOverAllScoreInPErcentage();
							}
							
							
							
							
						}
					}
					if(totalsQs !=0) {
						Float averageCompWiseScore = totalScore / totalsQs;
						comp.setReviewerScore(averageCompWiseScore);
						//save comp
						/**
						 * Reviewer Score will be the final acerage_score at UserCompetencyWiseScoreForAssessment level. So we will update it as well
						 */
						competencyWiseScoreForAssessmentService.updateReviewerScore(comp.getId(), averageCompWiseScore);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		} 
	}

}
