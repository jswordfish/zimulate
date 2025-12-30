package com.v2.competency.management.webservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.dtos.ExcelSheetReportStructureDto;
import com.v2.competency.management.dtos.MCQScoreForUserDto;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.UserSessionAttemptDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.repos.UserCompetencyWiseScoreForAssessmentRepo;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;
import com.v2.competency.management.util.ExcelReportUtil;

@RestController
@CrossOrigin
public class AI_ReportWebService {
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	VFTestUserSessionService userSessionService;
	
	@Autowired
	VFTestUserQuestionAnswerService answerService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService competencyWiseScoreForAssessmentService;
	
	@Autowired
	VFTestUserQuestionAnswerService questionAnswerService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentRepo repo;
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	VFTestUserSessionService sessionService;
	
	@Autowired
	QuestionService questionService;
	
	XmlMapper xmlMapper = new XmlMapper();
	
	ObjectMapper mapper = new ObjectMapper();
	
	
	@RequestMapping(value="internalUsers",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> internalUserAssessments( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<AssessmentMapper> assessments = assessmentMapperService.findInternalAssessmentTakers(companyId,PageRequest.of(pageNumber, 30));
	 List<AssessmentMapper> tests = assignUserLevelScore(assessments.getContent());
	 
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(assessments.getNumber());
	 res.setRecordsTo(assessments.getNumberOfElements());
	 res.setTotalNumberOfPages(assessments.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	
	 res.setList(tests);
	 for(AssessmentMapper test : tests) {
		 
		 System.out.println("internal user "+test.getEmail()+" ext "+test.getExternal());
	 }
	 return ResponseEntity.ok(res);
	}
	
	private List<AssessmentMapper> assignUserLevelScore(List<AssessmentMapper> users) {
		for(AssessmentMapper user : users) {
			User u = userService.findByEmail(user.getEmail(), user.getCompanyId());
			user.setScorePercent(u.getOverAllScore());
			user.setExternal(u.getExternal());
		}
		return users;
	}
	
	
	@RequestMapping(value="externalUsers",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> externalUserAssessments( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<AssessmentMapper> assessments = assessmentMapperService.findExternalAssessmentTakers(companyId,PageRequest.of(pageNumber, 30));
	 List<AssessmentMapper> tests = assignUserLevelScore(assessments.getContent());
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(assessments.getNumber());
	 res.setRecordsTo(assessments.getNumberOfElements());
	 res.setTotalNumberOfPages(assessments.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	
	 res.setList(tests);
	 for(AssessmentMapper test : tests) {
		 System.out.println("external user "+test.getEmail()+" ext "+test.getExternal());
	 }
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="aiAssesmentsTakerForUser",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> aiAssesmentsTakerForUser(   @RequestParam String companyId,  @RequestParam String email, 
           HttpSession session, @RequestParam String token) throws Exception{  
	List<VFTestUserSession> sessions = 	userSessionService.findAllUserSessionsByEmail(email, companyId);
		for(VFTestUserSession sess : sessions) {
			List<UserCompetencyWiseScoreForAssessment> comps = 	competencyWiseScoreForAssessmentService.findRecordsForUserAssessment2(email, sess.getTestIdentifier(), companyId);
			float totalScore = 0f;
			Integer count = 0;
				for(UserCompetencyWiseScoreForAssessment comp : comps) {
					if(comp.getAverageScore() != null) {
						totalScore += comp.getAverageScore();
						count++;
					}
				}
				if(count > 0) {
					sess.setFinalScore(totalScore/count);
				}
		}
	 return ResponseEntity.ok(sessions);
	}
	
	@RequestMapping(value="aiCompeetncyWiseForUserByTest",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> aiCompeetncyWiseForUserByTest(   @RequestParam String companyId,  @RequestParam String email, @RequestParam String testIdentifier,  @RequestParam(required = false) Integer attempt, 
           HttpSession session, @RequestParam String token) throws Exception{  
  //List<UserCompetencyWiseScoreForAssessment> comps = 	competencyWiseScoreForAssessmentService.findRecordsForUserAssessment2(email, testIdentifier, companyId);
		 List<UserCompetencyWiseScoreForAssessment> comps = 	competencyWiseScoreForAssessmentService.findRecordsForUserAssessment2Byattempt(email, testIdentifier, companyId, attempt);
  Set<UserCompetencyWiseScoreForAssessment> ret2 = new HashSet<>();
	ret2.addAll(comps);
  return ResponseEntity.ok(ret2);
	}
	
	
	@RequestMapping(value="generateScoresForAllUsersForAIApp",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> generateScoresForAllUsers(   @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
		int page = 0;
  
		 Page<AssessmentMapper> users = assessmentMapperService.findInternalAssessmentTakers(companyId,PageRequest.of(page, 30));
		 calculateAndUpdateScores((List<AssessmentMapper>)users.getContent());
			while(page < users.getTotalPages()) {
				page++;
				users = assessmentMapperService.findInternalAssessmentTakers(companyId,PageRequest.of(page, 30));
				 calculateAndUpdateScores((List<AssessmentMapper>)users.getContent());
				
			}
			return ResponseEntity.ok("success");
		 
	}
	
	private void calculateAndUpdateScores(List<AssessmentMapper> users) {
		for(AssessmentMapper user : users) {
				List<UserCompetencyWiseScoreForAssessment> comps =   competencyWiseScoreForAssessmentService.findAllRecordsForUserByCompanyIdNoPagination(user.getEmail(), user.getCompanyId());
				Float totalScore = 0f;
				Integer count = 0;
				for(UserCompetencyWiseScoreForAssessment c : comps) {
					Float scoreAtComopetencyLevel = 0f;
					Integer countCompetencyWise = 0;
					if(c.getQuestionMode().equalsIgnoreCase(Question_Type.MCQ.getType())) {
						totalScore += c.getAverageScore();
						count ++;
						scoreAtComopetencyLevel += c.getAverageScore();
						countCompetencyWise++;
					}
					else if(c.getQuestionMode().equalsIgnoreCase(Question_Type.SUBJECTIVE.getType())){
						String ids = c.getAnswerIds();
						String idArray[] = ids.split(",");
						for(String id : idArray) {
							Long i = Long.parseLong(id.trim());
							 VFTestUserQuestionAnswer ans =  questionAnswerService.getById(i);
							 if(ans.getOverallScoreIncaseOfSubjectiveByReviewer() != null) {
								 totalScore += ans.getOverallScoreIncaseOfSubjectiveByReviewer();
								 count ++;
								 scoreAtComopetencyLevel +=  ans.getOverallScoreIncaseOfSubjectiveByReviewer();
								 countCompetencyWise++;
							 }
							 else {
								 	if(ans.getOverallScoreIncaseOfSubjective() != null) {
								 		totalScore += ans.getOverallScoreIncaseOfSubjective();
										 count ++;
										 scoreAtComopetencyLevel +=  ans.getOverallScoreIncaseOfSubjective();
										 countCompetencyWise++;
								 	}
								 
							 }
						}
						
					}
					Float competencyWiseForUserScore = 0f;
						if(countCompetencyWise > 0 ) {
							competencyWiseForUserScore = scoreAtComopetencyLevel / countCompetencyWise;
							c.setAverageScore(competencyWiseForUserScore);
							repo.save(c);
						}
				}
				Float userScore = 0f;
				if(count > 0) {
					userScore = totalScore / count;
				}
				User u =   userService.findByEmail(user.getEmail(), user.getCompanyId());
				//System.out.println("email is "+u.getEmail()+" u.getexternal "+u.getExternal()+" role "+(u.getOrgHierarchy()==null?"null":u.getOrgHierarchy().getRoleOrDesig()));
				u.setOverAllScore(userScore);
				userService.saveOrUpdate(u);
		}
	}
	
	@RequestMapping(value = "/listAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> listAssessments( @RequestParam String token, @RequestParam String companyId, @RequestParam(required = false) Integer page){
		
		if(page == null) {
			page = 0;
		}
		
		Page<VFTest> tests =  testService.findTestsByCompanyId(companyId, PageRequest.of(page, 20));
		PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(tests.getNumber());
		 res.setRecordsTo(tests.getNumberOfElements());
		 res.setTotalNumberOfPages(tests.getTotalPages());
		 res.setSelectedPage(page + 1);
		 res.setList(tests.getContent());
		 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value = "/searchAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> searchAssessments( @RequestParam String token, @RequestParam String companyId, @RequestParam String search, @RequestParam(required = false) Integer page){
		
		if(page == null) {
			page = 0;
		}
		
		Page<VFTest> tests =  testService.findTestsContainingIdentifierText(search, companyId, PageRequest.of(page, 20));
		PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(tests.getNumber());
		 res.setRecordsTo(tests.getNumberOfElements());
		 res.setTotalNumberOfPages(tests.getTotalPages());
		 res.setSelectedPage(page + 1);
		 res.setList(tests.getContent());
		 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value = "/fetchUsersByAssessment", method = RequestMethod.GET)
	public ResponseEntity<?> fetchUsersByAssessment( @RequestParam String token, @RequestParam String companyId, @RequestParam String testIdentifier, @RequestParam(required = false) Integer page){
		if(page == null) {
			page = 0;
		}
		Page<VFTestUserSession> sessions =  sessionService.findUserSessionsForTest(testIdentifier, companyId, PageRequest.of(page, 20));
		PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(sessions.getNumber());
		 res.setRecordsTo(sessions.getNumberOfElements());
		 res.setTotalNumberOfPages(sessions.getTotalPages());
		 res.setSelectedPage(page + 1);
		 res.setList(sessions.getContent());
		 return ResponseEntity.ok(res);
	}
	
	@GetMapping("/downloadExcelForAIAssessment")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam String token, @RequestParam String companyId, @RequestParam String testIdentifier) throws IOException {
    	//List<UserCompetencyWiseScoreForAssessment> list =  competencyWiseScoreForAssessmentService.findAllRecordsForAssessmentByCompanyIdNoPagination(testIdentifier, companyId);
    	//Map<UserSessionAttemptDto, List<UserCompetencyWiseScoreForAssessment>> map = categorize(list);
    	//XSSFWorkbook workbook = ExcelReportUtil.createExcelReport(getHeadersAndDataForScenarioQ(testIdentifier, companyId)) ;
    	XSSFWorkbook workbook = ExcelReportUtil.createExcelReport(getHeadersAndDataForScenarioQ2(testIdentifier, companyId)) ;
        
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", testIdentifier+".xlsx"); 
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
    
    
    
private Map<String, ExcelSheetReportStructureDto> getHeadersAndDataForScenarioQ2( String testIdentifier,  String companyId) throws JsonParseException, JsonMappingException, IOException{
    	
    	Map<String, ExcelSheetReportStructureDto> allSheets_Data = new HashMap<>();
    	//Map<String,List<String>> headers = new HashMap<>();
    	VFTestUserQuestionAnswer mcq =  answerService.findMCQPresenceForTest(testIdentifier, companyId);
    	if(mcq != null) {
    		List<String> mcqHeaders = new ArrayList<>();
    		//mcqHeaders.add("Serial");
    		mcqHeaders.add("First Name");
    		mcqHeaders.add("Last Name");
    		mcqHeaders.add("Email");
    		mcqHeaders.add("Attempt");
    		mcqHeaders.add("Total Question Count");
    		mcqHeaders.add("Score in %");
    		ExcelSheetReportStructureDto sheetData = ExcelSheetReportStructureDto.builder().headers(mcqHeaders).build();
    		
    		List<MCQScoreForUserDto> mcqScoresForUsers =  answerService.findMCQScoreForUserByAssessment(testIdentifier, companyId);
    		List<List<Object>> mcqData = new ArrayList<>();
    		for(MCQScoreForUserDto score : mcqScoresForUsers) {
    			//row
    			List<Object> rowLevelDataForEachUser = new ArrayList<>();
    			rowLevelDataForEachUser.add(score.getFirstName());
    			rowLevelDataForEachUser.add(score.getLastName());
    			rowLevelDataForEachUser.add(score.getEmail());
    			rowLevelDataForEachUser.add(score.getAttempt());
    			rowLevelDataForEachUser.add(score.getCount());
    			rowLevelDataForEachUser.add(score.getAverageScore());
    			mcqData.add(rowLevelDataForEachUser);
    		}
    		sheetData.setData(mcqData);
    		sheetData.setHeaders(mcqHeaders);
    		allSheets_Data.put("MCQ", sheetData);
    	}
    	
    	List<VFTestUserQuestionAnswer> answers =  answerService.findAllSubjectiveAnswersForTest(testIdentifier, companyId);
    	Map<String, ExcelSheetReportStructureDto> scenarioMap =  ExcelReportUtil.classifyScenarioAnswers(answers, questionService);
    	for(String sheetName : scenarioMap.keySet()) {
    		allSheets_Data.put(sheetName, scenarioMap.get(sheetName));
    	}
    	
    		return allSheets_Data;
    }
    
    
    private Map<String, ExcelSheetReportStructureDto> getHeadersAndDataForScenarioQ( String testIdentifier,  String companyId) throws JsonParseException, JsonMappingException, IOException{
    	
    	Map<String, ExcelSheetReportStructureDto> allSheets_Data = new HashMap<>();
    	//Map<String,List<String>> headers = new HashMap<>();
    	VFTestUserQuestionAnswer mcq =  answerService.findMCQPresenceForTest(testIdentifier, companyId);
    	if(mcq != null) {
    		List<String> mcqHeaders = new ArrayList<>();
    		//mcqHeaders.add("Serial");
    		mcqHeaders.add("First Name");
    		mcqHeaders.add("Last Name");
    		mcqHeaders.add("Email");
    		mcqHeaders.add("Attempt");
    		mcqHeaders.add("Total Question Count");
    		mcqHeaders.add("Score in %");
    		ExcelSheetReportStructureDto sheetData = ExcelSheetReportStructureDto.builder().headers(mcqHeaders).build();
    		
    		List<MCQScoreForUserDto> mcqScoresForUsers =  answerService.findMCQScoreForUserByAssessment(testIdentifier, companyId);
    		List<List<Object>> mcqData = new ArrayList<>();
    		for(MCQScoreForUserDto score : mcqScoresForUsers) {
    			//row
    			List<Object> rowLevelDataForEachUser = new ArrayList<>();
    			rowLevelDataForEachUser.add(score.getFirstName());
    			rowLevelDataForEachUser.add(score.getLastName());
    			rowLevelDataForEachUser.add(score.getEmail());
    			rowLevelDataForEachUser.add(score.getAttempt());
    			rowLevelDataForEachUser.add(score.getCount());
    			rowLevelDataForEachUser.add(score.getAverageScore());
    			mcqData.add(rowLevelDataForEachUser);
    		}
    		sheetData.setData(mcqData);
    		sheetData.setHeaders(mcqHeaders);
    		allSheets_Data.put("MCQ", sheetData);
    	}
    	
    	List<VFTestUserQuestionAnswer> questions =  answerService.findDistincyScenarioQsForTest(testIdentifier, companyId);
    	Integer count = 1;
    		for(VFTestUserQuestionAnswer quest:questions) {//unique scenario questions only based on query
    			Question q = questionService.findById(Long.parseLong(quest.getQid()));
    			List<String> scenarioHeaders = new ArrayList<>();
				scenarioHeaders.add("First Name");
				scenarioHeaders.add("Last Name");
				scenarioHeaders.add("Email");
				scenarioHeaders.add("Scenario");
				scenarioHeaders.add("Text Response");
				scenarioHeaders.add("Audio Response");
    			
				if(q.getMultipleCompetenciesAssociatedWithQuestion() != null) {
					String json = "";
						if(quest.getReviewerAnalysisJson() != null) {
							json = quest.getReviewerAnalysisJson();
						}
						else {
							json = quest.getCustomAiAnalysisJson();
						}
    				
    				RolePlayInsightsDto custom = mapper.readValue(json.getBytes(), RolePlayInsightsDto.class);
    				
    					for(String competency : custom.getMapCompetenciesInsights().keySet()) {
    						RoleplayInsightsDetail detail = custom.getMapCompetenciesInsights().get(competency);
    						scenarioHeaders.add(competency+"_Observation");
    						scenarioHeaders.add(competency+"_Improvement Areas");
    						scenarioHeaders.add(competency+"_Score");
    					}
    				scenarioHeaders.add("Overall Observations");
    				scenarioHeaders.add("Overall Score");
    				ExcelSheetReportStructureDto sheetData = ExcelSheetReportStructureDto.builder().headers(scenarioHeaders).build();
    				List<VFTestUserQuestionAnswer> answers = answerService.findScenarioAnswersForTestQuestion(quest.getQuestion(), testIdentifier, companyId);
    				List<List<Object>> scenarioData = new ArrayList<>();
    				
    				for(VFTestUserQuestionAnswer ans : answers) {
    					List<Object> rowLevelDataForEachUser = new ArrayList<>();
    					rowLevelDataForEachUser.add(ans.getFirstName());
    					rowLevelDataForEachUser.add(ans.getLastName());
    					rowLevelDataForEachUser.add(ans.getEmail());
    					rowLevelDataForEachUser.add(ans.getQuestion());
    					rowLevelDataForEachUser.add(ans.getAnswer());
    					rowLevelDataForEachUser.add(ans.getAnswerAudioOrVideo()==null?"NA":ans.getAnswerAudioOrVideo());
    					
    					String jsonAns = null;
    					RolePlayInsightsDto customAns = null;
    						if(ans.getReviewerAnalysisJson() != null) {
    							jsonAns = ans.getReviewerAnalysisJson();
    							customAns = mapper.readValue(jsonAns.getBytes(), RolePlayInsightsDto.class);
    						}
    						else if(ans.getCustomAiAnalysisJson() != null){
    							jsonAns = ans.getCustomAiAnalysisJson();
    							customAns = mapper.readValue(jsonAns.getBytes(), RolePlayInsightsDto.class);
    						}
    						else {
    							customAns = custom;//is deep cloning required?
    							customAns.setOverAllScoreInPercent(0);
    							customAns.setOverAllObservations("Answer not relevant enough to initiate AI analysis");
    							
    							for(String competency : custom.getMapCompetenciesInsights().keySet()) {
    								RoleplayInsightsDetail detail = custom.getMapCompetenciesInsights().get(competency);
    								detail.setImprovementAreas("NA");
    								detail.setObservation("NA");
    								detail.setScoreInPercent(0);
    							}
    						}
        				 
        				
        					for(String competency : customAns.getMapCompetenciesInsights().keySet()) {
        						RoleplayInsightsDetail detail = customAns.getMapCompetenciesInsights().get(competency);
        						rowLevelDataForEachUser.add(detail.getObservation());
        						rowLevelDataForEachUser.add(detail.getImprovementAreas());
        						rowLevelDataForEachUser.add(detail.getScoreInPercent());
        					}
        					rowLevelDataForEachUser.add(customAns.getOverAllObservations());
        					rowLevelDataForEachUser.add(customAns.getOverAllScoreInPercent());
        			scenarioData.add(rowLevelDataForEachUser);
    				}
    				
    				sheetData.setData(scenarioData);
    				sheetData.setHeaders(scenarioHeaders);
    				allSheets_Data.put("Scenario_"+count, sheetData);
    				
    			}
    			else {
    				String json = "";
					if(quest.getReviewerAnalysisJson() != null) {
						json = quest.getReviewerAnalysisJson();
					}
					else {
						json = quest.getAiAnalysisJson();
					}
    				
    				
    				InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = mapper.readValue(json, InsightForScenarioBasedQuestion.class);
    				scenarioHeaders.add("Answer Relevance To Scenario");
    				scenarioHeaders.add("Problem Solving Skills");
    				scenarioHeaders.add("Decision Making Skills");
    				scenarioHeaders.add("Communication Skills");
    				scenarioHeaders.add("Creativity & Innovation");
    				scenarioHeaders.add("Ethical Considerations In Answer");
    				scenarioHeaders.add("Areas of Improvement");
    				scenarioHeaders.add("Overall Observation");
    				
    				scenarioHeaders.add("Score - Answer Relevance To Scenario");
    				scenarioHeaders.add("Score - Problem Solving Skills");
    				scenarioHeaders.add("Score - Decision Making Skills");
    				scenarioHeaders.add("Score - Communication Skills");
    				
    				scenarioHeaders.add("Score - Creativity & Innovation");
    				scenarioHeaders.add("Score - Ethical Considerations In Answer");
    				scenarioHeaders.add("Overall Score");
    				
    				ExcelSheetReportStructureDto sheetData = ExcelSheetReportStructureDto.builder().headers(scenarioHeaders).build();
    				List<VFTestUserQuestionAnswer> answers = answerService.findScenarioAnswersForTestQuestion(q.getQuestionText(), testIdentifier, companyId);
    				List<List<Object>> scenarioData = new ArrayList<>();
    				
    				for(VFTestUserQuestionAnswer ans : answers) {
    					List<Object> rowLevelDataForEachUser = new ArrayList<>();
    					rowLevelDataForEachUser.add(ans.getFirstName());
    					rowLevelDataForEachUser.add(ans.getLastName());
    					rowLevelDataForEachUser.add(ans.getEmail());
    					rowLevelDataForEachUser.add(ans.getQuestion());
    					rowLevelDataForEachUser.add(ans.getAnswer());
    					rowLevelDataForEachUser.add(ans.getAnswerAudioOrVideo());
    					
    					String jsonAns = "";
    					InsightForScenarioBasedQuestion insightForScenarioBasedQuestionAns =  null;
						if(ans.getReviewerAnalysisJson() != null) {
							jsonAns = ans.getReviewerAnalysisJson();
							insightForScenarioBasedQuestionAns = mapper.readValue(jsonAns, InsightForScenarioBasedQuestion.class);
						}
						else if(ans.getAiAnalysisJson() != null){
							jsonAns = ans.getAiAnalysisJson();
							insightForScenarioBasedQuestionAns = mapper.readValue(jsonAns, InsightForScenarioBasedQuestion.class);
						}
						else {
							insightForScenarioBasedQuestionAns = new InsightForScenarioBasedQuestion();;
							insightForScenarioBasedQuestionAns.setAnswerRelevanceToScenario("Answer Not relevant");
							
						}
    					
    					
    					
    					
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getAnswerRelevanceToScenario());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getProblemSolvingSkills());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getDecisionMakingSkills());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getCommunicationSkills());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getCreativityAndInnovation());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getEthicalConsiderationsInAnswer());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getAreasOfImprovement());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getOverAllObservations());
    					
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForAnswerRelevanceToScenarioInPercentage());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForProblemSolvingSkillsInPercentage());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForDecisionMakingSkillsInPercentage());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForCommunicationSkillsIPercentage());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForCreativityAndInnovation());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForEthicalConsiderationsInPercentage());
    					rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getOverAllScoreInPErcentage());
    				}
    				
    				sheetData.setData(scenarioData);
    				sheetData.setHeaders(scenarioHeaders);
    				allSheets_Data.put("Scenario_"+count, sheetData);
    			}
				count++;
    		}
    		return allSheets_Data;
    }
    
    private Map<UserSessionAttemptDto, List<UserCompetencyWiseScoreForAssessment>> categorize(List<UserCompetencyWiseScoreForAssessment> list){
    	Map<UserSessionAttemptDto, List<UserCompetencyWiseScoreForAssessment>> map = new HashMap<>();
    		for(UserCompetencyWiseScoreForAssessment comp : list) {
    			UserSessionAttemptDto key = UserSessionAttemptDto.builder().email(comp.getEmail()).attempt(comp.getAttempt()).testIdentifier(comp.getTestIdentifier()).build();
    			if(map.get(key) == null) {
    				key.setTestName(comp.getTestName());
    				List<UserCompetencyWiseScoreForAssessment> values = new ArrayList<>();
    				values.add(comp);
        			map.put(key, values);
    			}
    			else {
    				map.get(key).add(comp);
    			}
    			
    		}
    	return map;
    }

	
	
}
