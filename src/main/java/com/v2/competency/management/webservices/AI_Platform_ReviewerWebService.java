package com.v2.competency.management.webservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.RolePlayQuestionAnswer;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.repos.VFTestUserQuestionAnswerRepo;
import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.SyncAIInsightsGenService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;

@RestController
public class AI_Platform_ReviewerWebService {
	
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	AIResponseGeneratorService aiResponseGeneratorService;
	
	@Autowired
	VFTestUserSessionService sessionService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService userCompetencyWiseScoreForAssessmentService;
	
	@Autowired
	VFTestUserQuestionAnswerService questionAnswerService;
	
	@Autowired
	VFTestUserQuestionAnswerRepo questionAnswerRepo;
	
	@Autowired
	SyncAIInsightsGenService aiInsightsGenService;
	
	@Autowired
	QuestionService questionService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value="getAssessmentsForReview",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> getAssessments( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFTest> assessments =testService.findTestsByCompanyId(companyId, PageRequest.of(pageNumber, 30));
	 List<VFTest> tests = assessments.getContent();
	 		for(VFTest test : tests) {
	 			String uniqueSkills = aiResponseGeneratorService.fetchUniqueSkillsForTest(test);
	 			test.setUniqueSkills(uniqueSkills);
	 		}

	 	 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(assessments.getNumber());
		 res.setRecordsTo(assessments.getNumberOfElements());
		 res.setTotalNumberOfPages(assessments.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="getUsersForAssessments",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> getUsersForAssessments( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String testIdentifier, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFTestUserSession> sessions =sessionService.findUserSessionsForTest(testIdentifier, companyId, PageRequest.of(pageNumber, 15));
	 List<VFTestUserSession> list = sessions.getContent();
	 		

	 	 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(sessions.getNumber());
		 res.setRecordsTo(sessions.getNumberOfElements());
		 res.setTotalNumberOfPages(sessions.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	
	
	
	@RequestMapping(value="competencyWise",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> competencyWise(  @RequestParam String companyId, @RequestParam String testIdentifier,  @RequestParam String email, @RequestParam Integer attempt,
           HttpSession session, @RequestParam String token) throws Exception{  
	 VFTestUserSession userSession = sessionService.finfVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
	 System.out.println(" userSession is "+userSession+" email "+email+" tetsidentifier "+testIdentifier);
	 
	 List<VFTestUserQuestionAnswer> answers = questionAnswerService.findAllQAForUser(testIdentifier, email, companyId, attempt);
	 
	 Map<String, List<VFTestUserQuestionAnswer>> map = new HashMap<>();
		Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs = new HashMap<>();
		for(VFTestUserQuestionAnswer ans : answers) {
			Question q = questionService.findById(Long.parseLong(ans.getQid()));
		 	ans.setQ(q);
				if(ans.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
					//String ansChoice = ans.getAnswerChoiceIncaseOfMCQ() == null?"":ans.getAnswerChoiceIncaseOfMCQ();
					if(map.get(q.getParentCompetency()+"###"+q.getCompetency()) == null) {
						List<VFTestUserQuestionAnswer> list = new ArrayList<>();
						list.add(ans);
						map.put(q.getParentCompetency()+"###"+q.getCompetency(), list);
					}
					else {
						map.get(q.getParentCompetency()+"###"+q.getCompetency()).add(ans);
					}
					
				}
				else {
					//subjective qs
					/**
					 * not used for geneating insights on the fly yet
					 */
					if(mapSubjectiveQs.get(q.getParentCompetency()+"###"+q.getCompetency()) == null) {
						List<VFTestUserQuestionAnswer> list = new ArrayList<>();
						list.add(ans);
						mapSubjectiveQs.put(q.getParentCompetency()+"###"+q.getCompetency(), list);
					}
					else {
						mapSubjectiveQs.get(q.getParentCompetency()+"###"+q.getCompetency()).add(ans);
					}
				}
		}
		
		List<UserCompetencyWiseScoreForAssessment> ret = new ArrayList<>();
	
		for(String key: map.keySet()) {
			String comps[] = key.split("###");
			String parentComp = comps[0];
			String comp = comps[1];
			UserCompetencyWiseScoreForAssessment userCompetencyWiseScoreForAssessment = userCompetencyWiseScoreForAssessmentService.findUniqueRecord(email, userSession.getTestName(), testIdentifier, comp, parentComp, attempt, Question_Type.MCQ.getType(), companyId);
			if(userCompetencyWiseScoreForAssessment == null) {
				List<VFTestUserQuestionAnswer> list = map.get(key);
				Integer totalQs = list.size();
				Integer totalCorrect = 0;
				String ids = "";
				for(VFTestUserQuestionAnswer ans : list) {
					Boolean correct = true;
					if(ans.getAnswerChoiceIncaseOfMCQ() != null) {
						String choices[] = ans.getAnswerChoiceIncaseOfMCQ().split(",");
						String correctChoices[] = ans.getQ().getRightChoice().split(".");
						
						Set<String> set = new HashSet<>();
						for(String c:correctChoices){
							c = c.trim();
							set.add(c);
						}
					
						for(String c : choices) {
							c = c.trim();
							if(!set.contains(c)) {
								correct = false;
								break;
							}
						}
						
						if(correct) {
							totalCorrect = totalCorrect + 1;
						}
						
						ans.setMarkedCorrect(correct);
						ids += ans.getId()+",";
						questionAnswerRepo.save(ans);
					}
					
				}
				Float competencyWiseScore = (totalCorrect * 100.0f) / totalQs;
				ids = ids.substring(0, ids.length() - 1);
				UserCompetencyWiseScoreForAssessment competencyWiseScoreForAssessment = UserCompetencyWiseScoreForAssessment.builder().competency(comp)
						.parentCompetency(parentComp)
						.testName(userSession.getTestName())
						.averageScore(competencyWiseScore)
						.email(email)
						.attempt(attempt)
						.questionMode(Question_Type.MCQ.getType())
						.answerIds(ids)
						.testIdentifier(testIdentifier)
						.build();
				competencyWiseScoreForAssessment.setCompanyId(companyId);
				userCompetencyWiseScoreForAssessmentService.addUserCompetencyWiseScoreForCompetency(competencyWiseScoreForAssessment);
				ret.add(competencyWiseScoreForAssessment);
			}
			else {
				ret.add(userCompetencyWiseScoreForAssessment);
			}
		
		}
		List<UserCompetencyWiseScoreForAssessment> retSubjective = aiInsightsGenService.generateInsightsForScenarioBasedQsInSync(userSession.getTestName(), mapSubjectiveQs, testIdentifier, email, companyId, attempt);
																// aSyncAIInsightsGenService.generateInsightsForScenarioBasedQsInAsync(testName,  mapSubjectiveQs, testIdentifier, email, companyId, attempt);
		
		ret.addAll(retSubjective);
		
		Set<UserCompetencyWiseScoreForAssessment> ret2 = new HashSet<>();
		ret2.addAll(ret);
		
	 return ResponseEntity.ok(ret2);
	}
	
	@RequestMapping(value="answersByCompSubComp",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> answersByCompSubComp(  @RequestParam String companyId, @RequestParam String ids,
           HttpSession session, @RequestParam String token) throws Exception{  
	 String[] ansIds = ids.split(",");
	 
	List<VFTestUserQuestionAnswer> list = 	new ArrayList<>();
	
		for(String id : ansIds) {
				if(id != null && id.length() > 0) {
					VFTestUserQuestionAnswer a = 	questionAnswerRepo.findById(Long.parseLong(id)).get();
					list.add(a);
				}
		}
	 return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="updateScenarioAnswerWithReviewerComments",method=RequestMethod.POST)  
	@CrossOrigin
	public ResponseEntity<?> updateScenarioAnswerWithReviewerComments(  @RequestParam String companyId, @RequestBody VFTestUserQuestionAnswer answer,
           HttpSession session, @RequestParam String token) throws Exception{  
		VFTestUserQuestionAnswer answer2 = questionAnswerRepo.findById(answer.getId()).get();
		answer2.setReviewer1Email(answer.getReviewer1Email());
		answer2.setReviewer1FullName(answer.getReviewer1FullName());
		answer2.setReviewerAnalysisJson(answer.getReviewerAnalysisJson());
		answer2.setReviewerInsightsGenerated(true);
		
		String qid = answer2.getQid();
		Question q =  questionService.findById(Long.parseLong(qid));
		InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = null;
		RolePlayInsightsDto customAISightsForScenarioBasedQuestion = null;
		if(q.getAiInsightsPrompt() != null && q.getAiInsightsPrompt().trim().length() > 0) {
			customAISightsForScenarioBasedQuestion = mapper.readValue(answer.getReviewerAnalysisJson().getBytes(), RolePlayInsightsDto.class);
			answer2.setOverallScoreIncaseOfSubjectiveByReviewer(customAISightsForScenarioBasedQuestion.getOverAllScoreInPercent()*1.0f);
		}
		else {
			insightForScenarioBasedQuestion = mapper.readValue(answer.getReviewerAnalysisJson().getBytes(), InsightForScenarioBasedQuestion.class);
			answer2.setOverallScoreIncaseOfSubjectiveByReviewer(insightForScenarioBasedQuestion.getOverAllScoreInPErcentage());
		}
		
		
		answer2.setUpdateDate(new Date());
		questionAnswerRepo.save(answer2);
	 return ResponseEntity.ok("ok");
	}
	
	
//	@RequestMapping(value="mcqQuestionAnswers",method=RequestMethod.GET)  
//	@CrossOrigin
//	public ResponseEntity<?> mcqQuestionAnswers(  @RequestParam String companyId, @RequestParam String testIdentifier,  @RequestParam String email, @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam Integer attempt,
//           HttpSession session, @RequestParam String token) throws Exception{  
//	 
//	List<VFTestUserQuestionAnswer> list = 	questionAnswerService.findMCQQAForUserByCompetency(parentCompetency, competency, testIdentifier, email, companyId, attempt);
//	 return ResponseEntity.ok(list);
//	}
//	
//	@RequestMapping(value="subjectiveQuestionAnswers",method=RequestMethod.GET)  
//	@CrossOrigin
//	public ResponseEntity<?> subjectiveQuestionAnswers(  @RequestParam String companyId, @RequestParam String testIdentifier,  @RequestParam String email, @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam Integer attempt,
//           HttpSession session, @RequestParam String token) throws Exception{  
//	 
//	List<VFTestUserQuestionAnswer> list = 	questionAnswerService.findSubjectiveQAForUserByCompetency(parentCompetency, competency, testIdentifier, email, companyId, attempt);
//	 return ResponseEntity.ok(list);
//	}
//	
//	
	
	@RequestMapping(value="manuallyGenerateAIInsightsForScenarioassessment",method=RequestMethod.POST)  
	@CrossOrigin
	public ResponseEntity<?> manuallyGenerateAIInsightsForScenarioassessment( @RequestParam String companyId, @RequestParam Long answerId, 
           HttpSession session, @RequestParam String token) throws Exception{  
		
		VFTestUserQuestionAnswer ans =   questionAnswerService.getById(answerId);
		
		aiInsightsGenService.generateInsightsForScenarioBasedAnswer(ans.getId());
	 return ResponseEntity.ok("OK");
	}

}
