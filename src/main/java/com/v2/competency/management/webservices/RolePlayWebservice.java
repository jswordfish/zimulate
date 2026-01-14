package com.v2.competency.management.webservices;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.v2.competency.management.dtos.GreetingDto;
import com.v2.competency.management.dtos.InitRolePlayTestDTO;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.RoleplayTestSessionMetaData;
import com.v2.competency.management.entities.RolePlayQuestionAnswer;
import com.v2.competency.management.entities.RolePlayQuestionFollowUpLevel;
import com.v2.competency.management.entities.RoleplayDifficultyLevel;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.repos.RolePlayQuestionAnswerRepo;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.RelevancyCheckerService;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.service.VideoMergerServiceDJ;
import com.v2.competency.management.service.impl.PropertyConfig;
import com.v2.competency.management.service.impl.QueueManager;
import com.v2.competency.management.util.RolePlayParser;

@RestController
public class RolePlayWebservice {
	
	@Autowired
	RolePlayQuestionAnswerRepo repo; 
	
	@Autowired
	GeminiAudioVideoService geminiservice;
	
	@Autowired
	RolePlayQuestionAnswerService service;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	@Autowired
	RelevancyCheckerService relevanceService;
	
	@Autowired
	ASyncAIInsightsGenService aSyncAIInsightsGenService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	VideoMergerServiceDJ videoService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	QueueManager queueManager; 
	
	@Autowired
	VFRolePlayTestSessionRepo vfRolePlayTestSessionRepo;
	
	ObjectMapper map = new ObjectMapper();
	
	
    
    String defPrompt = " You are an active participant in a roleplay assessment, responding directly as the character in the scenario. Below is the transcript of the conversation so far:\r\n"
    		+ "\r\n"
    		+ "${TRANSCRIPT}\r\n"
    		+ "\r\n"
    		+ "Based on the employee's responses, generate a follow-up question that is engaging, tricky, and relevant to the conversation. Ensure that your question maintains a natural flow, challenges the employee's reasoning or explanation, and encourages deeper thinking. Strictly respond in the first person, as if you are the actual customer, client, or role-specific character engaging with the employee. Do NOT provide any context, explanations, or third-party narration—only return the follow-up question in a natural conversational tone.\r\n"
    		+ "\r\n"
    		+ "If the employee responds with uncertainty, such as 'I don't know' or 'No clue,' respond in character by questioning their lack of knowledge, such as 'How come you don’t know this when you are working here?' or 'I was expecting someone in your position to be aware of this.' After that, repeat the original question in simpler terms to give them another chance to answer. If the user answers something irrelevantm, then the next question should be I did not get your point, let me repeat the question again for you and then repeat the question. If the response is too short, acknowledge it by saying, 'That was a very brief answer. Could you elaborate on that?' and then rephrase the follow-up question to encourage a more detailed response. Ensure that follow-up questions build upon previous responses logically, making the conversation feel natural, dynamic, and immersive. Keep your tone and wording consistent with the role you are playing, whether it's a demanding customer, a curious client, or a strict manager. You are strictly allowed to talk in first person only and must stay within the role without breaking character at any point. ";
	
	String checkGreetingPrompt = "We are conducting a Role play simulation exercise facilitated by our AI platform. Here an end user speaks with an AI bot on any of Role play scenario.\r\n"
			+ "\r\n"
			+ "The role play conversation is initiated by the end user. You need to evaluate if the conversation  start message by end user is purely a 'greeting' message or a 'greeting message plus role play scenario based discussion start' or just a 'role play scenario based discussion start'. If your analysis qualifies end user's message  as a strictly greeting message, generate a brief, professional, response to the greeting. Otherwise, don't include a response if the end user's message is  not strictly a greeting message\r\n"
			+ "I am supplying you the end user message - ${MESSAGE}.\r\n"
			+ "Analyze what category user message falls in\r\n"
			+ "Provide the output strictly in the following **structured JSON** format:\r\n"
			+ "{\r\n"
			+ "  \"isStrictlyGreeting\" : false,\r\n"
			+ "  \"greetingResponse\" : null\r\n"
			+ "}";
    
	
	@RequestMapping(value = "/fetchRoleplayAssessmentsIdsByNames", method = RequestMethod.POST)
	public ResponseEntity<?> fetchRoleplayAssessmentsIdsByNames( @RequestParam String token, @RequestParam String email,  @RequestParam String companyId, @RequestBody List<RoleplayTestSessionMetaData> tests
			) throws IOException{
		List<Long> sessions = new ArrayList<>();
			for(RoleplayTestSessionMetaData dto : tests) {
				VFRolePlayTestSession sess =  rolePlayTestSessionService.findVFRolePlayTestSessionByEmail(email, companyId, dto.getTestIdentifier(), dto.getAttempt());
				sessions.add(sess.getId());
			}
		return ResponseEntity.ok(sessions);
	}
	
	@RequestMapping(value = "/fetchRoleplayAssessmentsByIds", method = RequestMethod.POST)
	public ResponseEntity<?> fetchRoleplayAssessmentsByIds( @RequestParam String token, @RequestParam String companyId, @RequestBody List<Long> ids
			) throws IOException{
		List<VFRolePlayTestSession> sessions = new ArrayList<>();
			for(Long id : ids) {
				sessions.add(vfRolePlayTestSessionRepo.findById(id).get());
			}
		return ResponseEntity.ok(sessions);
	}
    
    @PostMapping("/allRolePlayAnswersForRolePlayTestForUser")
	public List<RolePlayQuestionAnswer> initRolePlayRestQues(@RequestParam String email,
            @RequestParam String testName,
            @RequestParam Integer attempt,
            @RequestParam String companyId,
            @RequestParam String token) {
		testName = URLDecoder.decode(testName);
		return repo.findAllQAForUser(testName, email, companyId, attempt); 
		
	}
    
    @GetMapping("/get all test sessions paginated")
    public ResponseEntity<PaginatedResponseDto> getAllTestSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());
        Page<VFRolePlayTestSession> sessions = rolePlayTestSessionService.getAllTestSessions(pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * 10) + 1);
        response.setRecordsTo((page * 10) + sessions.getNumberOfElements());
        response.setTotalNumberOfRecords((int) sessions.getTotalElements());
        response.setTotalNumberOfPages(sessions.getTotalPages());
        response.setSelectedPage(page);
        response.setList(sessions.getContent());

        return ResponseEntity.ok(response);
    }
	
    @GetMapping("/get-sci-role-plays")
    public ResponseEntity<List<String>> getSCIRolePlays(@RequestParam String token){
    	List<String> rolePlays = Arrays.asList("Role Play - BASIC FINANCIAL PLANNING Training", "Role Play - BASIC FINANCIAL PLANNING Assessment",
    			"Role Play - Fact Finding by Agent", "Role Play - Conducting a Customer Knowledge Assessment (CKA) with a potential customer",
    			"Role Play - Explaining the 'Why': Basis of Recommendation", "Roleplay - Clarity & Transparency: Full Product Disclosure");
    	
    	return ResponseEntity.ok(rolePlays);	
    }
    
    @GetMapping("/get-all-tests-paginated")
    public ResponseEntity<PaginatedResponseDto> getAllTestsFiltered(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(required = false) String filter,   // industries
            @RequestParam(required = false) String search,   // testName
            @RequestParam(required = false) String sort,     // field=asc,field=desc
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PaginatedResponseDto response = rolePlayTestService
                .getRolePlayTestsFiltered(companyId, filter, search, sort, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/get all industries")
    public ResponseEntity<PaginatedResponseDto> getAllIndustries(
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        PaginatedResponseDto response = rolePlayTestService.getAllIndustriesPaginated(companyId, page);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/save or update role play test")
    public ResponseEntity<VFRolePlayTest> saveOrUpdateTest(@RequestBody VFRolePlayTest test, @RequestParam String token) {
        VFRolePlayTest savedTest = rolePlayTestService.saveOrUpdate(test);
        return ResponseEntity.ok(savedTest);
    }

    // 2. Filter by industry
    @GetMapping("/filter-by-company-and-industry")
    public ResponseEntity<PaginatedResponseDto> getTestsByCompanyAndIndustry(
            @RequestParam String companyId,
            @RequestParam String industry,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        PaginatedResponseDto response = rolePlayTestService.getRolePlayTestsByCompanyAndIndustry(companyId, industry, page);
        return ResponseEntity.ok(response);
    }
	
	@RequestMapping(value = "/initRolePlayTest", method = RequestMethod.GET)
	public ResponseEntity<?> initKbTest(@RequestParam String testName,  @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		
		testName = URLDecoder.decode(testName);
		VFRolePlayTest rolePlayTest =    rolePlayTestService.findUniqueRecord(testName, companyId);
			if(rolePlayTest == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test by this name "+testName+" does not exist");
			}
			rolePlayTest.getRoleplayAnalysisStructure().setRolePlayTest(null);
			return ResponseEntity.status(HttpStatus.OK).body(rolePlayTest);
	}
	
	@RequestMapping(value = "/initRolePlayTestWithNewStructure", method = RequestMethod.GET)
	public ResponseEntity<?> initKbTestNew(
	        @RequestParam String testName,
	        @RequestParam String companyId,
	        @RequestParam String token) throws Exception {

	    testName = URLDecoder.decode(testName);
	    VFRolePlayTest rolePlayTest = rolePlayTestService.findUniqueRecord(testName, companyId);

	    if (rolePlayTest == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Test by this name " + testName + " does not exist");
	    }

	    InitRolePlayTestDTO dto = new InitRolePlayTestDTO();
	    dto.setQuestionText(RolePlayParser.parseQuestionText(rolePlayTest.getQuestionText()));
	    dto.setProductInfo(RolePlayParser.parseProductInfo(rolePlayTest.getProductInfo()));
	    dto.setCompetitionInfo(RolePlayParser.parseCompetitionInfo(rolePlayTest.getCompetitionInfo()));

	    return ResponseEntity.ok(dto);
	}
	
	@RequestMapping(value = "/authenticateForRolePlay", method = RequestMethod.POST)
	public ResponseEntity<?> authenticateForRolePlay( @RequestParam String companyId, @RequestParam String token, @RequestBody User user){
		
		if(user.getEmail() != null && user.getPassword() != null) {
			String decodedpwd = new String(Base64.getDecoder().decode(user.getPassword()));
			if(user.getEmail().equals("demo@zimulate.me") && decodedpwd.equals("OQE_4534")) {
				return ResponseEntity.status(HttpStatus.OK).body("ok");
			}
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Credentials!!!");
	}
	
	
	@RequestMapping(value = "/getAttemptForRolePlaySession", method = RequestMethod.GET)
	public ResponseEntity<?> getAttemptRolePlaysession(@RequestParam String testName,@RequestParam String email,   @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		testName = URLDecoder.decode(testName);
		email = URLDecoder.decode(email);
		Integer count = rolePlayTestSessionService.findCountOfSessionsForUserForTest(email, companyId, testName);
		count++;
		return ResponseEntity.status(HttpStatus.OK).body((count));
	}
	
	@PostMapping("/videoTest")
	public String videoAnalysis(String prompt, String path, String token) throws IOException {
		
		String res = geminiservice.videoInput(prompt, path);
		
		return res;
		
	}
	
	
	@PostMapping("/nextRolePlayQuestion")
    public String nextQuestion(@RequestParam String email,
    							@RequestParam String firstName,
    							@RequestParam String lastName,
                               @RequestParam String testName,
                               @RequestParam Integer attempt,
                               @RequestParam String companyId,
                               @RequestParam String question,
                               @RequestParam String answer,
                               @RequestParam(required = false) String prompt, 
                               @RequestParam(required = false) String difficultyLevelOfRoleplay,
                               @RequestParam RolePlayQuestionFollowUpLevel followUpLevel,
                               @RequestParam(required = false) Boolean startedByUser,
                               @RequestParam(required = false) String rolePlayPersona, 
                               @RequestParam String token) throws IOException {
        
		testName = URLDecoder.decode(testName);
		VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
		String defaultPrompt = test.getDefaultQuestionPrompt();
			System.out.println("startedByUser "+startedByUser);
		if(startedByUser != null && startedByUser) {
			checkGreetingPrompt = checkGreetingPrompt.replace("${MESSAGE}", answer);
			String aiResponse = geminiService(checkGreetingPrompt);
			System.out.println(" initial message "+answer+" ai response "+aiResponse);
			map.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			GreetingDto startResponse = map.readValue(aiResponse, GreetingDto.class);
			
			if(startResponse.getIsStrictlyGreeting()) {
				saveUserIfNotExists(email, firstName, lastName, companyId);
				saveRolePlayAnswer(email, testName, companyId, attempt, question, answer, followUpLevel, difficultyLevelOfRoleplay, rolePlayPersona) ;       
				return 	"GREETING_RESPONSE - "+startResponse.getGreetingResponse();
			}
		}
		
        List<RolePlayQuestionAnswer> allQA = service.findAllQAForUser(testName, email, companyId, attempt);
        StringBuilder transcript = new StringBuilder();
        if(allQA.size() > 0) {
    		difficultyLevelOfRoleplay = allQA.get(0).getDifficultyLevel();
    	}
        else {
        	if(difficultyLevelOfRoleplay == null) {
        		difficultyLevelOfRoleplay = RoleplayDifficultyLevel.EASY.getLevel();
        	}
        	else {
        		try {
					difficultyLevelOfRoleplay = RoleplayDifficultyLevel.valueOf(difficultyLevelOfRoleplay).getLevel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Invalid RoleplayDifficultyLevel value "+difficultyLevelOfRoleplay+". Right values are "+RoleplayDifficultyLevel.values());
				}
        	}
        }

        
        for (RolePlayQuestionAnswer qa : allQA) {
            transcript.append("Q: ").append(qa.getQuestion()).append("\n");
            transcript.append("A: ").append(qa.getAnswer()).append("\n");
        }
        
        if (!allQA.isEmpty()) {
            transcript.append("A: ").append(answer).append("\n");
        }
//        
//     

	   
	    transcript.append("Q: ").append(question).append("\n");
	    transcript.append("A: ").append(answer).append("\n");
	    
	    if (followUpLevel == RolePlayQuestionFollowUpLevel.START) {
	    	saveUserIfNotExists(email, firstName, lastName, companyId);
	    }

	    
	    if (followUpLevel == RolePlayQuestionFollowUpLevel.FOLLOW_UP_5) {
	    	saveRolePlayAnswer(email, testName, companyId, attempt, question, answer, followUpLevel, difficultyLevelOfRoleplay, rolePlayPersona) ;       
	    	VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	    	session.setRolePlayPersona(rolePlayPersona);
	    	session.setDifficultyLevel(difficultyLevelOfRoleplay);
	    	
	    	
	    	session.setTestIdentifier(testName);
	    	session = rolePlayTestSessionService.saveOrUpdate(session);
	        return "END";
	    }
	    String nextQuestion = null;
        if(!test.getIsTestStartByUser()) {
        	nextQuestion = geminiService(transcript.toString(), defaultPrompt);
        }
        else {
        	nextQuestion = geminiService(transcript.toString(), difficultyLevelOfRoleplay, test, rolePlayPersona);
        }

        saveRolePlayAnswer(email, testName, companyId, attempt, question, answer, followUpLevel, difficultyLevelOfRoleplay, rolePlayPersona) ;       

        return nextQuestion;
	}
	
	private void saveRolePlayAnswer(String email, String testName, String companyId, Integer attempt, String question, String answer, RolePlayQuestionFollowUpLevel followUpLevel, String difficultyLevelOfRoleplay, String persona) {
		RolePlayQuestionAnswer newQA = new RolePlayQuestionAnswer();
        newQA.setCreateDate(new Date());
        newQA.setEmail(email);
        newQA.setTestName(testName);
        newQA.setCompanyId(companyId);
        newQA.setAttempt(attempt);
        newQA.setQuestion(question);
        newQA.setAnswer(answer);  
        newQA.setFollowUpLevel(followUpLevel);  
        newQA.setDifficultyLevel(difficultyLevelOfRoleplay);
        newQA.setRolePlayPersona(persona);
        repo.save(newQA);
	}
	
	
	private void saveUserIfNotExists(String email, String firstName, String lastName, String companyId) {
		if(userService.findByEmail(email, companyId) == null) {
    		User user = User.builder().email(email)
					.firstName(firstName)
					.lastName(lastName)
					.password("12345")
					.build();
			user.setCompanyId(companyId);
			user.setExternal(true);
			userService.saveOrUpdate(user);
    	}
	}
	
	@GetMapping("/findRolePlayTestsByTestName")
	public VFRolePlayTest findRolePlayTest(@RequestParam String companyId, String testName, String token) {
		return rolePlayTestService.findRolePlayTestsByTestName(companyId, testName);
	}
	
	
	
	@PostMapping("/nextRolePlayQuestionWithVideo")
	public String nextQuestion(@RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String testName,
            @RequestParam Integer attempt,
            @RequestParam String companyId,
            @RequestParam String question,
            @RequestParam String answer,
            @RequestParam(required = false) String prompt, 
            @RequestParam RolePlayQuestionFollowUpLevel followUpLevel,
            @RequestParam String token,
            @RequestParam MultipartFile videoFile) throws IOException {

		testName = URLDecoder.decode(testName);
		String defaultPrompt = (prompt != null && !prompt.trim().isEmpty()) ? URLDecoder.decode(prompt) : defPrompt;
		
		
		String baseLoc = "/opt/eAssess/apache-tomcat-9.0.93/webapps/ROOT/audio";

		
		String loc = baseLoc + File.separator + companyId + File.separator + testName + File.separator + email + File.separator + attempt;
		File folder = new File(loc);
		folder.mkdirs();

		
		String fileName = System.currentTimeMillis() + "_" + followUpLevel + ".mp4";
		File destinationFile = new File(folder.getAbsolutePath() + File.separator + fileName);

		
		try {
		    videoFile.transferTo(destinationFile);
		} catch (IOException e) {
		    throw new IOException("Error saving video file", e);
		}
		
		List<RolePlayQuestionAnswer> allQA = service.findAllQAForUser(testName, email, companyId, attempt);
		StringBuilder transcript = new StringBuilder();
		
		for (RolePlayQuestionAnswer qa : allQA) {
		transcript.append("Q: ").append(qa.getQuestion()).append("\n");
		transcript.append("A: ").append(qa.getAnswer()).append("\n");
		}
		
		if (!allQA.isEmpty()) {
		transcript.append("A: ").append(answer).append("\n");
		}
		
		transcript.append("Q: ").append(question).append("\n");
		transcript.append("A: ").append(answer).append("\n");
		
		if (followUpLevel == RolePlayQuestionFollowUpLevel.START) {
		if (userService.findByEmail(email, companyId) == null) {
		User user = User.builder().email(email)
		     .firstName(firstName)
		     .lastName(lastName)
		     .password("12345")
		     .external(true)
		     .build();
		userService.saveOrUpdate(user);
		}
		}
		
		if (followUpLevel == RolePlayQuestionFollowUpLevel.FOLLOW_UP_3) {
		VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
		session.setTestIdentifier(testName);
		String mergedVideoPath = videoService.mergeVideosFinal(companyId, testName, email, attempt);
		session.setVideoLink(mergedVideoPath);
		String mergedFileName = Paths.get(mergedVideoPath).getFileName().toString();
		String fileUrl = config.getFileServerBaseUrl()+session.getCompanyId()+"/"+session.getTestName()+"/"+session.getEmail()+"/"+session.getAttempt()+"/"+mergedFileName;
		session.setVideoUrl(fileUrl);
		session = rolePlayTestSessionService.saveOrUpdate(session);

		aSyncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(testName, email, companyId, session, transcript.toString(), mergedVideoPath);

		return "END ";

		}
		
		String nextQuestion = geminiService(transcript.toString(), defaultPrompt); 
		
		RolePlayQuestionAnswer newQA = new RolePlayQuestionAnswer();
		newQA.setCreateDate(new Date());
		newQA.setEmail(email);
		newQA.setTestName(testName);
		newQA.setCompanyId(companyId);
		newQA.setAttempt(attempt);
		newQA.setQuestion(nextQuestion);
		newQA.setAnswer(answer);
		newQA.setFollowUpLevel(followUpLevel);
		repo.save(newQA);
		
		return nextQuestion;
		}
	
	private String geminiService( String onlyPrompt) throws IOException {

    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName(); 
        
    	

	    String output = textInput(projectId, location, modelName, onlyPrompt);
	    output = output.replaceFirst("```json\\n", "");
	    output = output.replaceFirst("\\n```", "");
	    return output;
    	
	}

    private String geminiService(String transcript, String defaultPrompt) throws IOException {

    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName(); 
        
    	String textPrompt = null;
    		if(defaultPrompt == null) {
    			textPrompt = defPrompt.replace("${TRANSCRIPT}", transcript);
    		}
    		else {
    			textPrompt = defaultPrompt.replace("${TRANSCRIPT}", transcript);
    		}

	    String output = textInput(projectId, location, modelName, textPrompt);
	    output = output.replaceFirst("```json\\n", "");
	    output = output.replaceFirst("\\n```", "");
	    
	    return output;
    	
	}
    
    private String geminiService(String transcript,  String difficultLevel, VFRolePlayTest test, String persona) throws IOException {

    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName(); 
        
        String questionPrompt = test.getDefaultQuestionPrompt().replace("${TRANSCRIPT}", transcript);
        if(persona == null) {
        	questionPrompt = questionPrompt.replace("${PERSONA}", "");
        }
        else {
        	questionPrompt = questionPrompt.replace("${PERSONA}", persona);
        }
        String scenario = test.getQuestionText();
        if(difficultLevel.equalsIgnoreCase(RoleplayDifficultyLevel.EASY.getLevel())) {
        	//do nothing
        }
        else if(difficultLevel.equalsIgnoreCase(RoleplayDifficultyLevel.MEDIUM.getLevel())) {
        	scenario += ""+System.lineSeparator()+test.getProductInfo();
        }
        else if(difficultLevel.equalsIgnoreCase(RoleplayDifficultyLevel.HARD.getLevel())) {
        	scenario += ""+System.lineSeparator()+test.getProductInfo()+System.lineSeparator()+test.getCompetitionInfo();
        }
        else {
        	throw new RuntimeException("No difficulty level associated");
        }
        questionPrompt = questionPrompt.replace("${SCENARIO}", scenario);
	    String output = textInput(projectId, location, modelName, questionPrompt);
	    output = output.replaceFirst("```json\\n", "");
	    output = output.replaceFirst("\\n```", "");
	    return output;
    	
	}
    
	public static String textInput(String projectId, String location, String modelName, String textPrompt) throws IOException {
			
			try (VertexAI vertexAI = new VertexAI(projectId, location)) {
				GenerationConfig generationConfig =
		                GenerationConfig.newBuilder()
		                   // .setResponseMimeType("application/json") // This is the key line
		                    .build();
			      GenerativeModel model = new GenerativeModel(modelName, generationConfig, vertexAI);
	
			      GenerateContentResponse response = model.generateContent(textPrompt);
			      String output = ResponseHandler.getText(response);
			      output = output.replaceFirst("```json\\n", "");
				    output = output.replaceFirst("\\n```", "");
			      return output;
			    }
			
		}

//	private RolePlayQuestionFollowUpLevel getNextLevel(RolePlayQuestionFollowUpLevel currentLevel) {
//        switch (currentLevel) {
//            case START:
//                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_1;
//            case FOLLOW_UP_1:
//                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_2;
//            case FOLLOW_UP_2:
//                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_3;
//            default:
//                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_3;
//        }
//    }
	
	@PostMapping("/pollForRoleplayInsights")
	public String pollForRoleplayInsights(@RequestParam String email,
	                                      @RequestParam String testName,
	                                      @RequestParam Integer attempt,
	                                      @RequestParam String companyId,
	                                      @RequestParam String token) throws IOException {

	    VFRolePlayTestSession session = rolePlayTestSessionService
	            .findVFRolePlayTestSessionByEmail(email, companyId, testName, attempt);

	    if (session == null) {
	        return "-100"; 
	    }

	    
	    System.out.println("Polling for insights for session ID: " + session.getId());

	    if (session.getEvaluationFailed() != null && session.getEvaluationFailed()) {
	        return "-100"; 
	    }

	    if (session.getInsightsJson() != null) {
	        
	        return map.writerWithDefaultPrettyPrinter().writeValueAsString(session);
	    }

	    return waitForInsights(session); 
	}
	
	
	@PostMapping("/pollForRoleplayVideoInsights")
	public String pollForRoleplayVideoInsights(@RequestParam String email,
	                                      @RequestParam String testName,
	                                      @RequestParam Integer attempt,
	                                      @RequestParam String companyId,
	                                      @RequestParam String token) throws IOException {
		
		
		VFRolePlayTestSession session = rolePlayTestSessionService.findVFRolePlayTestSessionByEmail(email, companyId, testName, attempt);
		if (session == null) {
	        return "-100"; // Invalid session
	    }

	    Integer position = queueManager.getPositionInQueue(session);
	    System.out.println("Queue position for session ID " + session.getId() + " = " + position);

	    if (position != null && position >= 0) {
	        return position.toString(); // Return queue position
	    }

	    System.out.println("Session is NOT in queue, checking for insights...");

	    if (session.getEvaluationFailed() != null && session.getEvaluationFailed()) {
	        return "-100"; // Evaluation failed
	    }

	    if (session.getVideoInsightsJson() != null) {
	    	return session.getVideoInsightsJson() + "," + "\n" + "Video Path : " + session.getVideoLink();
	    }

	    return waitForInsights(session);
	}
	
	private String waitForInsights(VFRolePlayTestSession session) {
	    int maxWaitTime = 5000; // 5 seconds
	    int elapsed = 0;
	    int interval = 1000; // 1 second interval

	    while (elapsed < maxWaitTime) {
	        try {
	            Thread.sleep(interval);
	            elapsed += interval;

	            // Re-fetch the session
	            session = rolePlayTestSessionService.findVFRolePlayTestSessionById(session.getId());

	            if (session.getEvaluationFailed() != null && session.getEvaluationFailed()) {
	                return "-100"; // Evaluation failed
	            }

	            if (session.getInsightsJson() != null) {
	                return session.getInsightsJson(); // Insights available
	            }
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            break;
	        }
	    }

	    return "-100"; // Timeout
	}

		
	}
	
	
//	@Component
//	public class QueueManager {
//
//	    private BlockingQueue<String> dataQueue = new LinkedBlockingQueue<>(); // Or any other type
//
//	    public BlockingQueue<String> getDataQueue() {
//	        return dataQueue;
//	    }
//
//	    // Add methods to add items to the queue, if needed
//	    public void enqueue(String item) {
//	        try {
//	            dataQueue.put(item);
//	        } catch (InterruptedException e) {
//	            Thread.currentThread().interrupt(); // Restore interrupt status
//	            // Handle interruption (e.g., log, rethrow)
//	        }
//	    }
//	}
//	
//	@Component
//	public class QueueListenerThread {
//
//	    private Thread thread;
//	    private final BlockingQueue<String> queue;
//
//	    @Autowired
//	    public QueueListenerThread(QueueManager queueManager) {
//	        this.queue = queueManager.getDataQueue();
//	    }
//
//	    @PostConstruct
//	    public void startListener() {
//	        thread = new Thread(() -> {
//	            try {
//	                while (!Thread.currentThread().isInterrupted()) {
//	                    String item = queue.take();
//	                    processItem(item);
//	                }
//	            } catch (InterruptedException e) {
//	                Thread.currentThread().interrupt();
//	                System.out.println("Listener thread interrupted.");
//	            } finally {
//	                System.out.println("Listener thread exiting.");
//	            }
//	        });
//	        thread.start();
//	    }
//
//	    @PreDestroy
//	    public void stopListener() {
//	        if (thread != null && thread.isAlive()) {
//	            thread.interrupt();
//	            try {
//	                thread.join();
//	            } catch (InterruptedException e) {
//	                Thread.currentThread().interrupt();
//	            }
//	        }
//	    }
//
//	    private void processItem(String item) {
//	        try {
//	            String[] parts = item.split("\\|");
//	            if (parts.length != 6) {
//	                System.err.println("Invalid queue item format: " + item);
//	                return;
//	            }
//
//	            String testName = parts[0];
//	            String email = parts[1];
//	            String companyId = parts[2];
//	            Long sessionId = Long.parseLong(parts[3]); // Assuming ID is a Long
//	            String transcript = parts[4];
//	            String mergedVideoPath = parts[5];
//
//	            VFRolePlayTestSession session = rolePlayTestSessionService.findVFRolePlayTestSessionById(sessionId);
//	            if (session == null) {
//	                System.err.println("Session not found for ID: " + sessionId);
//	                return;
//	            }
//
//	            // Call the async insights generation service
//	            aSyncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(
//	                testName, email, companyId, session, transcript, mergedVideoPath
//	            );
//
//	        } catch (Exception e) {
//	            System.err.println("Error processing queue item: " + e.getMessage());
//	            e.printStackTrace();
//	        }
//	    }
//	}
	
//	public void processInsights(String data) {
//	    String[] parts = data.split(",");
//	    Long sessionId = Long.parseLong(parts[0]); // Convert ID back to Long
//	    String testName = parts[1];
//	    String email = parts[2];
//	    String companyId = parts[3];
//	    String transcript = parts[4];
//	    String videoPath = parts[5];
//
//	    // Retrieve session from database
//	    VFRolePlayTestSession session = rolePlayTestSessionService.findVFRolePlayTestSessionById(sessionId);
//	    
//	    if (session == null) {
//	        System.out.println("Session not found for ID: " + sessionId);
//	        return;
//	    }
//
//	    // Call insights generation logic
//	    aSyncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(
//	        testName, email, companyId, session, transcript, videoPath
//	    );
//	}


