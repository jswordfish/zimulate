package com.v2.competency.management.webservices;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.communication.dto.AITestResponseCommunication;
import com.googlecloud.vertex.ai.communication.dto.ExpectedResponseCommunication;
import com.googlecloud.vertex.ai.communication.dto.QuestionSpecific;
import com.googlecloud.vertex.ai.dto.AITestResponse;
import com.googlecloud.vertex.ai.dto.ExpectedResponse2;
import com.googlecloud.vertex.ai.dto.VertexResponse;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.insights.dto.InsightsDto;
import com.v2.competency.management.common.util.VFTestInterviewFlowUtil;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.ResponseDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Source;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.repos.VFTestUserQuestionAnswerRepo;
import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
public class SaveVFQAService {
	
	@Autowired
	VFTestUserSessionService sessionService;
	
	
	@Autowired
	VFTestUserQuestionAnswerService answerService;
	
	@Autowired
	VFTestUserQuestionAnswerRepo questionAnswerRepo;
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	AIResponseGeneratorService aiResponseGeneratorService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService userCompetencyWiseScoreForAssessmentService;
	
	@Autowired
	ASyncAIInsightsGenService aSyncAIInsightsGenService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	 XmlMapper xmlMapper = new XmlMapper();
	 
	 @Autowired
	 QuestionService questionService;
	 
	 @Autowired
	 GeminiAudioVideoService audioVideoService;
	 
	final  String PUBLIC_TEST_LINK = "https://voiceflow-ai-frontened.vercel.app/?company_id={COMPANY_ID}&test_identifier={TEST_IDENTIFIER}&associatedSkills={ASSOCIATED_SKILLS}";
	
	@RequestMapping(value="saveQuestion",method=RequestMethod.POST)  
    public ResponseEntity<?> saveQuestion( @RequestBody VFTestUserQuestionAnswer answer,  
           HttpSession session, @RequestParam String token) throws Exception{  
		
		try {
			Objects.requireNonNull(answer.getCompanyId());
			Objects.requireNonNull(answer.getTestIdentifier());
			Objects.requireNonNull(answer.getEmail());
			Objects.requireNonNull(answer.getFirstName());
			Objects.requireNonNull(answer.getLastName());
			Objects.requireNonNull(answer.getQuestion());
			Objects.requireNonNull(answer.getAnswer());
			Objects.requireNonNull(answer.getTimeOfLastQuestion());
			Objects.requireNonNull(answer.getQid());
			Objects.requireNonNull(answer.getQuestionType());
			
			if(answer.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
				 Integer score =  processMCQAnswer(answer);
				 answer.setScoreInPercent(score);
			}
			
			//Objects.requireNonNull(answer.getTimeTakenToAnswerInMinutes());
			System.out.println("fn "+answer.getFirstName());
			Long currentTime = Calendar.getInstance().getTimeInMillis();
			Integer timeOfAnswer =new Double( ((currentTime - answer.getTimeOfLastQuestion())*1.0d / 60000)).intValue();
			answer.setTimeTakenToAnswerInMinutes(timeOfAnswer);
			
			if (answer.getVideoLink() != null && !answer.getVideoLink().isEmpty()) {
	            System.out.println("Saving video link: " + answer.getVideoLink());
	        }
			
			answerService.create(answer);
			
			
			//respon
			ResponseDto dto =  ResponseDto.builder().data("ok").build();
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResponseDto dto =  ResponseDto.builder().data("error "+e.getMessage()).build();
			//return ResponseEntity.ok(dto);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * For external users
	 */
	private void createAssessmentAndUserRecordIOfNotExists( String email, String firstName, String lastName,  String testIdentifier,  String companyId) {
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
		String path1 = test.getPath1()==null?"NA":test.getPath1();
		String path2 = test.getPath2()==null?"NA":test.getPath2();
		String path3 = test.getTestName();
		String path4 = "AI Platform";
		String path5 = "NA";
		
		User user2 = userService.findByEmail(email, companyId);
		User user = null;
			if(user2 != null){
				user = user2;
			}
		
		AssessmentMapper assessmentMapper =  assessmentMapperService.findByEmailAndTestNameAndPaths(email, test.getTestName(), path1, path2, path3, path4, path5, companyId);
			if(assessmentMapper == null) {
				//Create AssessmentMapper record
				assessmentMapper =  AssessmentMapper.builder().email(email)
						.firstName(firstName)
						.lastName(lastName)
						.testLink("NA")
						.testName(test.getTestName())
						.external(user==null?true:false)
						.path1(path1)
						.path2(path2)
						.path3(path3)
						.path4(path4)
						.path5(path5)
						.typePath1("NA")
						.typePath2("NA")
						.typePath3("NA")
						.typePath4("NA")
						.typePath5("NA")
						.build();
				assessmentMapper.setCompanyId(companyId);
				assessmentMapperService.saveOrUpdate(assessmentMapper);
				
				//Create User record 
				if(user == null) {
					user = User.builder().email(email)
							.firstName(firstName)
							.lastName(lastName)
							.password("12345")
							.build();
					user.setCompanyId(companyId);
					user.setExternal(true);
					userService.saveOrUpdate(user);
				}
				
			}
	}
	
	
	/**
	 * Latest
	* //@RequestPart VFTestUserQuestionAnswer answer
	 */
	@PostMapping(value="saveAnswer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})  
    public ResponseEntity<?> saveAnswer(@RequestPart(required = false) MultipartFile file,  @RequestPart String ans,  
           HttpSession session, @RequestParam String token, @RequestParam(required = false) String fileNameWithExtension) throws Exception{  
		
		try {
			
			VFTestUserQuestionAnswer answer = mapper.readValue(ans.getBytes(), VFTestUserQuestionAnswer.class);
			createAssessmentAndUserRecordIOfNotExists(answer.getEmail(), answer.getFirstName(), answer.getLastName(), answer.getTestIdentifier(),  answer.getCompanyId());
			Objects.requireNonNull(answer.getCompanyId());
			Objects.requireNonNull(answer.getTestIdentifier());
			Objects.requireNonNull(answer.getEmail());
			Objects.requireNonNull(answer.getFirstName());
			Objects.requireNonNull(answer.getLastName());
			Objects.requireNonNull(answer.getQuestion());
			Objects.requireNonNull(answer.getAnswer());
			Objects.requireNonNull(answer.getTimeOfLastQuestion());
			Objects.requireNonNull(answer.getQid());
			Objects.requireNonNull(answer.getQuestionType());
			
			if(file != null) {
				System.out.println("ext "+FilenameUtils.getExtension(file.getOriginalFilename()));
				Integer att = sessionService.findCountOfSessionsForUserFotTest(answer.getEmail(), answer.getCompanyId(), answer.getTestIdentifier()) + 1;
				String baseLoc = config.getFileServerPath();
				String loc = "";
				 loc = baseLoc + java.io.File.separator + answer.getCompanyId() +java.io.File.separator + answer.getTestIdentifier() +File.separator+answer.getEmail()+File.separator+att+File.separator+answer.getQid();
				 File folder = new File(loc);
				 folder.mkdirs();
				//Files.createParentDirs(folder);
				
				if(fileNameWithExtension != null) {
					 File fl = new File( folder.getAbsolutePath()+File.separator +fileNameWithExtension);
					 file.transferTo(fl);
				}
				else {
					 File fl = new File( folder.getAbsolutePath()+File.separator +file.getResource().getFilename());
					 file.transferTo(fl);
				}
				
				
				
				String fileUrl = config.getFileServerBaseUrl()+"/"+answer.getCompanyId()+"/"+answer.getTestIdentifier()+"/"+answer.getEmail()+"/"+att+"/"+answer.getQid()+"/"+(fileNameWithExtension!=null?fileNameWithExtension:file.getResource().getFilename());
				System.out.println(fileUrl);
				
				if (fileNameWithExtension.equalsIgnoreCase("mp4") || fileNameWithExtension.equalsIgnoreCase("avi") || fileNameWithExtension.equalsIgnoreCase("mov")) {
	                answer.setVideoLink(fileUrl);
	            } else {
	                answer.setAnswerAudioOrVideo(fileUrl);
	            }
	        }
				
				
			
			
			
			if(answer.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
				 Integer score =  processMCQAnswer(answer);
				 answer.setScoreInPercent(score);
			}
			
			//Objects.requireNonNull(answer.getTimeTakenToAnswerInMinutes());
			System.out.println("fn "+answer.getFirstName());
			Long currentTime = Calendar.getInstance().getTimeInMillis();
			Integer timeOfAnswer =new Double( ((currentTime - answer.getTimeOfLastQuestion())*1.0d / 60000)).intValue();
			answer.setTimeTakenToAnswerInMinutes(timeOfAnswer);
			answerService.create(answer);
			
			
			//respon
			ResponseDto dto =  ResponseDto.builder().data("ok").build();
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResponseDto dto =  ResponseDto.builder().data("error "+e.getMessage()).build();
			//return ResponseEntity.ok(dto);
			throw new RuntimeException(e);
		}
	}
	
	Integer   processMCQAnswer(VFTestUserQuestionAnswer answer) {
		if(answer.getQuestionType() != null && answer.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
			if(answer.getAnswerChoiceIncaseOfMCQ() != null) {
				String ans[] = answer.getAnswerChoiceIncaseOfMCQ().split(",");
				Question q = questionService.findById(Long.valueOf(answer.getQid()));
				String rightChoices[] = q.getRightChoice().split(",");
				Set<String> set = new HashSet<>();
				for(String c : rightChoices) {
					set.add(c);
				}
				boolean correct = true;
				for(String a : ans) {
					if(!set.contains(a)) {
						correct = false;
						break;
					}
				}
				return correct==true?100:0; 
			}
		}
			
			return 0;
	}
	
	//VFTestUserSession
	@RequestMapping(value="saveTestSession",method=RequestMethod.POST)  
    public ResponseEntity<?> saveTestSession( @RequestBody VFTestUserSession sess,  
           HttpSession session, @RequestParam String token) throws Exception{ 
		Integer att = sessionService.findCountOfSessionsForUserFotTest(sess.getEmail(), sess.getCompanyId(), sess.getTestIdentifier()) + 1;
		sess.setAttempt(att);
		sessionService.saveOrUpdate(sess)	;
		generateScore( sess.getTestName(),sess.getTestIdentifier(), sess.getEmail(), sess.getCompanyId(), att);
		ResponseDto dto =  ResponseDto.builder().data("ok").build();
		return ResponseEntity.ok(dto);
	}
	
	
	private void generateScore(String testName, String testIdentifier, String email, String companyId, Integer attempt) {
		System.out.println("in generateScore "+testIdentifier+" "+email+" "+companyId+" "+attempt);
		List<VFTestUserQuestionAnswer>  answers =  answerService.findAllQAForUser(testIdentifier, email, companyId, attempt);
		System.out.println("size answers "+answers.size());
		Map<String, List<VFTestUserQuestionAnswer>> map = new HashMap<>();
		Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs = new HashMap<>();
		for(VFTestUserQuestionAnswer ans : answers) {
			Question q = questionService.findById(Long.parseLong(ans.getQid()));
			ans.setQ(q);
				if(q.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
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
		
		System.out.println("mapSubjectiveQs size "+mapSubjectiveQs.keySet().size());
		
		/**
		 * Generating score for MCQ questions
		 */
		for(String key : map.keySet()) {
			String comps[] = key.split("###");
			String parentComp = comps[0];
			String comp = comps[1];
			List<VFTestUserQuestionAnswer> list = map.get(key);
			Integer totalQs = list.size();
			Integer totalCorrect = 0;
			String ids = "";
			for(VFTestUserQuestionAnswer ans : list) {
				Boolean correct = true;
				if(ans.getAnswerChoiceIncaseOfMCQ() != null) {
					String choices[] = ans.getAnswerChoiceIncaseOfMCQ().split(",");
					String correctChoices[] = ans.getQ().getRightChoice().split(",");
					Set<String> set = new HashSet<>();
					for(String c:correctChoices){
						c = c.trim();
						set.add(c);
					}
				//	List<String> listOfCorrectChoices = Arrays.asList(choices);
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
					.testName(testName)
					.averageScore(competencyWiseScore)
					.email(email)
					.attempt(attempt)
					.questionMode(Question_Type.MCQ.getType())
					.answerIds(ids)
					.testIdentifier(testIdentifier)
					.build();
			competencyWiseScoreForAssessment.setCompanyId(companyId);
			userCompetencyWiseScoreForAssessmentService.addUserCompetencyWiseScoreForCompetency(competencyWiseScoreForAssessment);
		}
		/**
		 * Generate insights for scenario based questions
		 */
		aSyncAIInsightsGenService.generateInsightsForScenarioBasedQsInAsync(testName,  mapSubjectiveQs, testIdentifier, email, companyId, attempt);
	}
	
	//Voiceflow based tests
//	@RequestMapping(value="createTest",method=RequestMethod.POST)  
//    public ResponseEntity<?> createTest( @RequestBody List<CompetencyQuestion> competencies,  @RequestParam String testIdentifier, @RequestParam String testName,  @RequestParam String companyId,
//           HttpSession session, @RequestParam String token) throws Exception { 
//		if(competencies.size() == 0) {
//			return ResponseEntity.ok("No competencies passed");
//		}
//		
//		if(competencies.size() > 10) {
//			return ResponseEntity.ok("No competencies passed");
//		}
//		
//		CompetencyTest competencyTest =  VFTestInterviewFlowUtil.calculate(competencies);
//		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest);
//		VFTest test = VFTest.builder().testIdentifier(testIdentifier)
//				.testXml(xml)
//				.testName(testName)
//				.build();
//		test.setCompanyId(companyId);
//		String link = PUBLIC_TEST_LINK;
//		link = link.replace("{COMPANY_ID}", companyId);
//		link = link.replace("{TEST_IDENTIFIER}", testIdentifier);
//		String skills = aiResponseGeneratorService.fetchUniqueSkillsForTestHashSeparated(test);
//		skills = URLEncoder.encode(skills);
//		link = link.replace("{ASSOCIATED_SKILLS}", skills);
//		test.setPublicTestUrl(link);
//		testService.saveOrUpdate(test);
//		return ResponseEntity.ok(link);
//	}
	
	//Voiceflow based tests
	@RequestMapping(value="createTest",method=RequestMethod.POST)  
    public ResponseEntity<?> createTest(@RequestBody VFTest test, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception { 
		
		
		if(test.getTestIdentifier() == null || test.getTestName() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test Identifier or Name not present");
		}
		
		if(!test.getQuestionSource().equals(Question_Source.AI.getSource())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("For creating Non AI based Test, call the createTestKBRandomOrFixed API");
		}
		
		if(test.getCompetencyTest() == null || test.getCompetencyTest().getCompetencies()== null || test.getCompetencyTest().getCompetencies().size() == 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No competencies passed");
		}
		
		List<CompetencyQuestion> competencies =  test.getCompetencyTest().getCompetencies();
		
		
		if(competencies.size() > 3) {
			return ResponseEntity.ok("No of Competencies can not be greater than 3");
		}
		
		CompetencyTest competencyTest =  VFTestInterviewFlowUtil.calculate(competencies);
		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest);
		test.setTestXml(xml);
		test.setCompanyId(companyId);
		String link = PUBLIC_TEST_LINK;
		link = link.replace("{COMPANY_ID}", companyId);
		link = link.replace("{TEST_IDENTIFIER}", test.getTestIdentifier());
		String skills = aiResponseGeneratorService.fetchUniqueSkillsForTestHashSeparated(test);
		skills = URLEncoder.encode(skills);
		link = link.replace("{ASSOCIATED_SKILLS}", skills);
		test.setPublicTestUrl(link);
		testService.saveOrUpdate(test);
		return ResponseEntity.ok(link);
	}
	
	@RequestMapping(value="createTestKBRandomOrFixed",method=RequestMethod.POST)  
    public ResponseEntity<?> createTestKBRandomOrFixed( @RequestBody VFTest test,   @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception { 
		if(test.getTestIdentifier() == null || test.getTestName() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test Identifier or Name not present");
		}
		
		if(test.getQuestionSource().equals(Question_Source.AI.getSource())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("For creating AI based Test, call the createTest API");
		}
		
		if(test.getCompetencyTest() == null || test.getCompetencyTest().getKbCompetencies()== null || test.getCompetencyTest().getKbCompetencies().size() == 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No KB based competencies passed");
		}
		
		CompetencyTest competencyTest =  test.getCompetencyTest();
		if(test.getQuestionSource().equals(Question_Source.QUESTION_BANK_FIXED.getSource())) {
			List<CompetencyDto> comps =  competencyTest.getKbCompetencies();
			for(CompetencyDto c : comps) {
				List<Long> qids =  c.getQuestionIds();
				List<Question> questions = new ArrayList<>();
					for(Long id : qids) {
						Question q =  questionService.findById(id);
						questions.add(q);
					}
				c.setQuestions(questions);
				
			}
		}
		else {
			List<CompetencyDto> comps =  competencyTest.getKbCompetencies();
			for(CompetencyDto c : comps) {
				if(c.getNoOfQuestionsToBeAsked() == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Number of Questions field null for competency - "+c.getCompetency());
				}
				
				if(c.getQuestionType() == null || Question_Type.valueOf(c.getQuestionType()) == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Question Type mandatory for KB based Random test for competency - "+c.getCompetency());
				}
			}
		}
		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest);
		
		test.setTestXmlForKB(xml);
		
		test.setCompanyId(companyId);
		String link = config.getAitestUrl();
		link = link.replace("$[COMPANY_ID]", companyId);
		link = link.replace("$[TEST_IDENTIFIER]", test.getTestIdentifier());
//		String skills = aiResponseGeneratorService.fetchUniqueSkillsForTestHashSeparated(test);
		test.setPublicTestUrl(link);
//		skills = URLEncoder.encode(skills);
//		link = link.replace("{ASSOCIATED_SKILLS}", skills);
//		test.setPublicTestUrl(link);
		testService.saveOrUpdate(test);
		return ResponseEntity.ok(link);
	}
	
	@RequestMapping(value="checkIfTestExistsByIdentifier",method=RequestMethod.GET)  
    public ResponseEntity<?> checkIfTestExistsByIdentifier(@RequestParam String testIdentifier, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		VFTest test =  testService.findByTestIdentifier(testIdentifier, companyId);
			if(test == null) {
				return ResponseEntity.ok("no");
			}
			return ResponseEntity.ok("yes");
	}
	
	@RequestMapping(value="checkIfTestExistsByTestName",method=RequestMethod.GET)  
    public ResponseEntity<?> checkIfTestExistsByTestName(@RequestParam String testName, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		VFTest test =  testService.findByTestName(testName, companyId);
			if(test == null) {
				return ResponseEntity.ok("no");
			}
			return ResponseEntity.ok("yes");
	}
	
	
	
	
	@RequestMapping(value="vfTestsByPage",method=RequestMethod.GET)  
    public ResponseEntity<?> vfTestsByPage( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFTest> users = testService.findTestsByCompanyId(companyId,  PageRequest.of(pageNumber, 15));
	 List<VFTest> tests = users.getContent();
	 	for(VFTest test : tests) {
	 		String skills = aiResponseGeneratorService.fetchUniqueSkillsForTest(test);
	 		test.setUniqueSkills(skills);
	 		String xml = test.getTestXml();
	 			if(xml != null) {
	 				CompetencyTest t = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
	 		 		test.setCompetencyTest(t);
	 			}
	 			else {
	 				xml = test.getTestXmlForKB();
	 				CompetencyTest t = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
	 		 		test.setCompetencyTest(t);
	 			}
	 		
	 	}
	 	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(users.getNumber());
	 res.setRecordsTo(users.getNumberOfElements());
	 res.setTotalNumberOfPages(users.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	// res.setPreviousPage(pageNumber  -1);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
 }
	
	@RequestMapping(value="searchVFTestsByPage",method=RequestMethod.GET)  
    public ResponseEntity<?> searchVFTestsByPage( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, 
    		@RequestParam String search,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFTest> users = testService.findTestsContainingTestNameText(search, companyId,  PageRequest.of(pageNumber, 15));
	 List<VFTest> tests = users.getContent();
	 	for(VFTest test : tests) {
	 		String skills = aiResponseGeneratorService.fetchUniqueSkillsForTest(test);
	 		test.setUniqueSkills(skills);
	 		String xml = test.getTestXml();
	 			if(xml != null) {
	 				CompetencyTest t = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
	 		 		test.setCompetencyTest(t);
	 			}
	 			else {
	 				xml = test.getTestXmlForKB();
	 				CompetencyTest t = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
	 		 		test.setCompetencyTest(t);
	 			}
	 		
	 	}
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(users.getNumber());
	 res.setRecordsTo(users.getNumberOfElements());
	 res.setTotalNumberOfPages(users.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	// res.setPreviousPage(pageNumber  -1);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
 }
	
	
	@RequestMapping(value="fetchUserSessionsForTest",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchUserSessionsForTest( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String testIdentifier,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFTestUserSession> sessions = sessionService.findUserSessionsForTest(testIdentifier, companyId, PageRequest.of(pageNumber, 15)) ;
	 List<VFTestUserSession> cont = sessions.getContent();
	 	for(VFTestUserSession sess : cont) {
	 		if(sess.getAiAnalysisJson() != null) {
	 			AITestResponse testResponse = mapper.readValue(sess.getAiAnalysisJson(), AITestResponse.class);
	 		//	sess.setAiAnalysisJson(null);
	 			sess.setTestResponse(testResponse);
	 			List<VFTestUserQuestionAnswer> answers = 	answerService.findAllQAForUser(testIdentifier, sess.getEmail(), companyId, sess.getAttempt());
	 			System.out.println(answers.size());
	 			/**
	 			 * Adding ques ans to response
	 			 */
	 			int count = 1;
	 			for(VFTestUserQuestionAnswer qa : answers) {
	 				qa.setQid(""+count);
	 				count++;
	 			}
	 			testResponse.setQuesAnswers(answers);
	 			/**
	 			 * End Adding ques ans to response
	 			 */
	 			Map<String, String> map_qid_ans  = getAnswer_QIDMap(answers);
	 			System.out.println("map "+map_qid_ans+" testIdentifier "+testIdentifier+" att "+sess.getAttempt());
	 			
	 			if(testResponse != null && testResponse.getResponse() != null) {
	 				for( VertexResponse res :  testResponse.getResponse().getList()) {
		 				System.out.println("setting answer");
		 				res.setAnswer(map_qid_ans.get(res.getQuestionId()));
		 			}
	 			}
	 			
	 		}
	 		
	 		if(sess.getAiAnalysisCommunicationJson() != null) {
	 			AITestResponseCommunication testResponse = mapper.readValue(sess.getAiAnalysisJson(), AITestResponseCommunication.class);
	 		//	sess.setAiAnalysisJson(null);
	 			sess.setTestResponseCommunication(testResponse);
	 			List<VFTestUserQuestionAnswer> answers = 	answerService.findAllQAForUser(testIdentifier, sess.getEmail(), companyId, sess.getAttempt());
	 			System.out.println(answers.size());
	 			Map<String, String> map_qid_ans  = getAnswer_QIDMap(answers);
	 			System.out.println("map "+map_qid_ans+" testIdentifier "+testIdentifier+" att "+sess.getAttempt());
	 			if(testResponse != null && testResponse.getResponse() != null) {
	 				for( QuestionSpecific res :  testResponse.getResponse().getAnswers()) {
		 				System.out.println("setting answer");
		 				res.setAnswer(map_qid_ans.get(res.getQuestionId()));
		 			}
	 			}
	 			
	 		}
	 	}
	 
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(sessions.getNumber());
	 res.setRecordsTo(sessions.getNumberOfElements());
	 res.setTotalNumberOfPages(sessions.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(cont);
	 return ResponseEntity.ok(res);
	
	}
	
	private Map<String, String> getAnswer_QIDMap(List<VFTestUserQuestionAnswer> answers ){
		Map<String, String> map_qid_ans = new HashMap<>();
		Integer count = 1;
		for(VFTestUserQuestionAnswer ans : answers) {
			//map_qid_ans.put(ans.getQid(), ans.getAnswer());
			map_qid_ans.put(count+"", ans.getAnswer());
			count++;
		}
		return map_qid_ans;
	}
	
	
	@RequestMapping(value="generateAIAnalysis",method=RequestMethod.POST)  
    public ResponseEntity<?> generateAIAnalysisCompetencyInsights(  @RequestParam String testIdentifier, @RequestParam String email,  @RequestParam String companyId, @RequestParam Integer attempt,
           HttpSession session, @RequestParam String token) throws Exception { 
		
		VFTestUserSession userSession = sessionService.finfVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
		if(userSession == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found for "+email+", "+testIdentifier+", "+companyId+" and "+attempt+" combination");
		}
		
		List<VFTestUserQuestionAnswer>  answers =  answerService.findAllQAForUser(testIdentifier, email, companyId, attempt);
		ExpectedResponse2 response =   aiResponseGeneratorService.generateAnalysisUsingGemini(answers, testIdentifier, userSession.getFirstName()+" "+userSession.getLastName(), companyId);
		String transcript = "";
		for(VFTestUserQuestionAnswer answer : answers) {
			transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
			transcript +=  answer.getAnswer()+System.lineSeparator();
		}
		
		
		AITestResponse testResponse = AITestResponse.builder()
				.response(response)
				.transcript(transcript)
				.build();
		
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResponse);
		userSession.setAiAnalysisJson(json);
		sessionService.saveOrUpdate(userSession);
		userSession.setTestResponse(testResponse);
		return ResponseEntity.ok(userSession);
	}
	
	@RequestMapping(value="generateAIAnalysisCompetencyInsightsForScenarioBasedQuestion",method=RequestMethod.POST)  
    public ResponseEntity<?> generateAIAnalysisCompetencyInsightsForScenarioBasedQuestion(  @RequestBody VFTestUserQuestionAnswer answer,
           HttpSession session, @RequestParam String token) throws Exception { 
		
		VFTestUserQuestionAnswer answer2 = questionAnswerRepo.findById(answer.getId()).get();
		InsightForScenarioBasedQuestion insightForScenarioBasedQuestion =  aiResponseGeneratorService.generateAnalysisForScenarioBasedQuestion(answer2);
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insightForScenarioBasedQuestion);
		answer2.setAiInsightsGenerated(true);
		answer2.setAiAnalysisJson(json);
		questionAnswerRepo.save(answer2);
		return ResponseEntity.ok(insightForScenarioBasedQuestion);
	}
	
	
	@RequestMapping(value="generateAIAnalysisCompetencyInsights",method=RequestMethod.POST)  
    public ResponseEntity<?> generateAIAnalysis(  @RequestParam String testIdentifier, @RequestParam String email,  @RequestParam String companyId, @RequestParam Integer attempt,
           HttpSession session, @RequestParam String token) throws Exception { 
		
		VFTestUserSession userSession = sessionService.finfVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
		if(userSession == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found for "+email+", "+testIdentifier+", "+companyId+" and "+attempt+" combination");
		}
		
		List<VFTestUserQuestionAnswer>  answers =  answerService.findAllQAForUser(testIdentifier, email, companyId, attempt);
		InsightsDto response =   aiResponseGeneratorService.generateAnalysisUsingGeminiForCompetencyInsights(answers, testIdentifier, userSession.getFirstName()+" "+userSession.getLastName(), companyId);
		String transcript = "";
		for(VFTestUserQuestionAnswer answer : answers) {
			transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
			transcript +=  answer.getAnswer()+System.lineSeparator();
		}
		
		
		AITestResponse testResponse = AITestResponse.builder()
				.insightsResponse(response)
				.transcript(transcript)
				.build();
		
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResponse);
		userSession.setAiAnalysisJson(json);
		sessionService.saveOrUpdate(userSession);
		userSession.setTestResponse(testResponse);
		return ResponseEntity.ok(userSession);
	}
	
	@RequestMapping(value="generateAIAnalysisForCommunication",method=RequestMethod.POST)  
    public ResponseEntity<?> generateAIAnalysisForCommunication(  @RequestParam String testIdentifier, @RequestParam String email,  @RequestParam String companyId, @RequestParam Integer attempt,
           HttpSession session, @RequestParam String token) throws Exception { 
		
		VFTestUserSession userSession = sessionService.finfVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
		if(userSession == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session found for "+email+", "+testIdentifier+", "+companyId+" and "+attempt+" combination");
		}
		
		List<VFTestUserQuestionAnswer>  answers =  answerService.findAllQAForUser(testIdentifier, email, companyId, attempt);
		ExpectedResponseCommunication response =   aiResponseGeneratorService.generateAnalysisCommunicationUsingGemini(answers, testIdentifier, userSession.getFirstName()+" "+userSession.getLastName(), companyId);
		String transcript = "";
		int count = 1;
		for(VFTestUserQuestionAnswer answer : answers) {
		//	transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
			transcript += count+". "+answer.getQuestion()+System.lineSeparator();
			transcript +=  answer.getAnswer()+System.lineSeparator();
			transcript +=  System.lineSeparator();
			count++;
		}
		
		
		AITestResponseCommunication testResponse = AITestResponseCommunication.builder()
				.response(response)
				.transcript(transcript)
				.build();
		
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(testResponse);
		userSession.setAiAnalysisCommunicationJson(json);
		sessionService.saveOrUpdate(userSession);
		userSession.setTestResponseCommunication(testResponse);
		return ResponseEntity.ok(userSession);
	}
	

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity maxUploadSizeExceeded(MaxUploadSizeExceededException e) {
	    // handle it here
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Audio/Video Size exceeded 10 MB");
	}
}
