package com.v2.competency.management.webservices;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v2.competency.management.dtos.AssessmentTraversalPath;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.impl.PropertyConfig;
@RestController
public class AssessmentMapperWebService {
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PropertyConfig config;
	
//	static String eAssessTokenAPI = "http://15.207.3.196:8080/assessment/generateToken";
//	
//	static String eAssessGetTestLinkApi = "http://15.207.3.196:8080/assessment/getTestLinkForUser?token=${TOKEN}&companyId=${COMPANY_ID}&testName=${TEST_NAME}"
//			+ "&userId=${EMAIL}&candidateId=${CANDIDATE_ID}&firstName=${FIRST_NAME}&lastName=${LAST_NAME}";
//	
//	
//	static String eAssessCheckTestStatusForUser = "http://15.207.3.196:8080/assessment/checkIfTestUnderGoneByUser?token=${TOKEN}&companyId=${COMPANY_ID}&testName=${TEST_NAME}"
//			+ "&userId=${EMAIL}";
	
	
static String eAssessTokenAPI = "";
	
	static String eAssessGetTestLinkApi = "";
	
	
	static String eAssessCheckTestStatusForUser = "";
	
	
	@PostConstruct
	public void init() {
		eAssessTokenAPI = config.getTokenApi();
		eAssessGetTestLinkApi = config.getGetTestLinkApi();
		eAssessCheckTestStatusForUser = config.getCheckTestStatusForUserApi();
	} 
	
	
	
	
	static ObjectMapper mapper = new ObjectMapper();
	
	static String tokenInputJson = "";
	static {
		User u = new User();
		u.setEmail("test@essess.com");
		u.setPassword("12345");
		try {
			tokenInputJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(u);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/assignReviewersToAssessmentForUser", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> assignReviewersToAssessmentForUser(  @RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			 @RequestParam String testName,
			 @RequestParam Boolean external,
			 @RequestParam String path1,
			 @RequestParam(required = false) String path2,
			 @RequestParam(required = false)  String path3,
			 @RequestParam(required = false)  String path4,
			 @RequestParam(required = false)  String path5,
			 
			 @RequestParam(required = false) String path1Type,
			 @RequestParam(required = false) String path2Type,
			 @RequestParam(required = false)  String path3Type,
			 @RequestParam(required = false)  String path4Type,
			 @RequestParam(required = false)  String path5Type,
			 @RequestBody List<String> reviewers
			)
			throws Exception {
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
		if(tenant == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Company Id"+companyId);
		}
		
		for(String reviewer : reviewers) {
			User user = userService.findByEmail(reviewer, companyId);
				if(user == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Reviewer"+reviewer);
				}
		}
		
		String tok = getTokenFromEAssess();
		System.out.println("token eassess "+tok);
		
		for(String reviewer : reviewers) {
			User user = userService.findByEmail(reviewer, companyId);
			String url = eAssessGetTestLinkApi;
			url = url.replace("$[TOKEN]", URLEncoder.encode( tok) );
			url = url.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
			url = url.replace("$[TEST_NAME]", URLEncoder.encode(testName));
			String reviewer1 = reviewer+"$$$"+testName+"$$$"+email+"$$$"+firstName+"$$$"+lastName;
			url = url.replace("$[EMAIL]", URLEncoder.encode(reviewer1));
			url = url.replace("$[CANDIDATE_ID]", "NA");
			url = url.replace("$[FIRST_NAME]", URLEncoder.encode(user.getFirstName()));
			url = url.replace("$[LAST_NAME]", URLEncoder.encode(user.getLastName()));
			System.out.println("url is "+url);
			URL url2 = new URL(url);
			String testLink = getStringResponseFromAPI(url2);
			
			
			AssessmentMapper assessmentMapper = AssessmentMapper.builder().email(reviewer)
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.testLink(testLink)
					.testName(testName)
					.path1(path1)
					.path2(path2==null?"NA":path2)
					.path3(path3==null?"NA":path3)
					.path4(path4==null?"NA":path4)
					.path5(path5==null?"NA":path5)
					.typePath1(path1Type==null?"NA":path1Type)
					.typePath2(path2Type==null?"NA":path2Type)
					.typePath3(path3Type==null?"NA":path3Type)
					.typePath4(path4Type==null?"NA":path4Type)
					.typePath5(path5Type==null?"NA":path5Type)
					.reviewMode(true)
					.reviewedUser(firstName+" "+lastName)
					.reviewedUserEmail(email)
					.build();
			
			if(path1Type.equalsIgnoreCase("Consolidated Assessments")) {
				assessmentMapper.setConsolidatedAssessments(true);
				assessmentMapper.setPath3(testName);
			}
			
			if(testName.toUpperCase().startsWith("FEEDBACK")) {
				assessmentMapper.setTypePath3("survey");
			}
			assessmentMapper.setCompanyId(companyId);
			assessmentMapper.setCompanyName(tenant.getCompanyName());
			assessmentMapperService.saveOrUpdate(assessmentMapper);
			
		}
		 return ResponseEntity.ok("ok");
	
	}
	
	@RequestMapping(value = "/assignAssessmentToUser", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> assignAssessmentToUser( @RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			 @RequestParam String testName,
			 @RequestParam Boolean external,
			 @RequestParam String path1,
			 @RequestParam(required = false) String path2,
			 @RequestParam(required = false)  String path3,
			 @RequestParam(required = false)  String path4,
			 @RequestParam(required = false)  String path5,
			 
			 @RequestParam(required = false) String path1Type,
			 @RequestParam(required = false) String path2Type,
			 @RequestParam(required = false)  String path3Type,
			 @RequestParam(required = false)  String path4Type,
			 @RequestParam(required = false)  String path5Type
			)
			throws Exception {
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
			if(tenant == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Company Id"+companyId);
			}
		
		String tok = getTokenFromEAssess();
		System.out.println("token eassess "+tok);
		String url = eAssessGetTestLinkApi;
		url = url.replace("$[TOKEN]", URLEncoder.encode( tok) );
		url = url.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
		url = url.replace("$[TEST_NAME]", URLEncoder.encode(testName));
		url = url.replace("$[EMAIL]", URLEncoder.encode(email));
		url = url.replace("$[CANDIDATE_ID]", "NA");
		url = url.replace("$[FIRST_NAME]", URLEncoder.encode(firstName));
		url = url.replace("$[LAST_NAME]", URLEncoder.encode(lastName));
		System.out.println("url is "+url);
		URL url2 = new URL(url);
		String testLink = getStringResponseFromAPI(url2);
		System.out.println("testLink is "+testLink);
		
		if(external) {
			User usr = User.builder().email(email)
					.lastName(lastName)
					.firstName(firstName)
					.external(external)
					.password("12345")
					.build();
			usr.setCompanyId(companyId);
			usr.setCompanyName(tenant.getCompanyName());
			userService.saveOrUpdate(usr);
		}
		
		AssessmentMapper assessmentMapper = AssessmentMapper.builder().email(email)
				.firstName(firstName)
				.lastName(lastName)
				.testLink(testLink)
				.testName(testName)
				.external(external)
				.path1(path1)
				.path2(path2==null?"NA":path2)
				.path3(path3==null?"NA":path3)
				.path4(path4==null?"NA":path4)
				.path5(path5==null?"NA":path5)
				.typePath1(path1Type==null?"NA":path1Type)
				.typePath2(path2Type==null?"NA":path2Type)
				.typePath3(path3Type==null?"NA":path3Type)
				.typePath4(path4Type==null?"NA":path4Type)
				.typePath5(path5Type==null?"NA":path5Type)
				.build();
		
		if(path1Type.equalsIgnoreCase("Consolidated Assessments")) {
			assessmentMapper.setConsolidatedAssessments(true);
			assessmentMapper.setPath3(testName);
		}
		
		if(testName.toUpperCase().startsWith("FEEDBACK")) {
			assessmentMapper.setTypePath3("survey");
		}
		assessmentMapper.setCompanyId(companyId);
		assessmentMapper.setCompanyName(tenant.getCompanyName());
		assessmentMapperService.saveOrUpdate(assessmentMapper);
		
		return ResponseEntity.ok(testLink);

	}
	
	public static String getTokenFromEAssess() {
		try {
			URL url2 = new URL(eAssessTokenAPI);
			HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			byte[] postData = tokenInputJson.getBytes( StandardCharsets.UTF_8 );
			int postDataLength = postData.length;
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			   wr.write( postData );
			}
			String data = getResponse(conn);
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	public static String getStringResponseFromAPI(URL url2) {
		try {
			HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
			conn.setRequestMethod("GET");
			int responseCode = conn.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if(responseCode != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Can not fetch test URL");
			}
//			conn.setRequestProperty("Content-Type", "application/json");
//			byte[] postData = tokenInputJson.getBytes( StandardCharsets.UTF_8 );
//			int postDataLength = postData.length;
//			conn.setRequestProperty("charset", "utf-8");
//			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
//			conn.setUseCaches(false);
//			conn.setDoOutput(true);
//			try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
//			   wr.write( postData );
//			}
			String data = getResponse(conn);
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} 
	}
	
	public static String getResponse(HttpURLConnection con) {
		if(con!=null){
			
			try {
				
			   BufferedReader br = 
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));
						
			   String input;
			   String output="";
						
			   while ((input = br.readLine()) != null){
				   output +=input;
			   }
			   br.close();
			   return output;
						
			} catch (IOException e) {
			   e.printStackTrace();
			}
					
		       }
				
		   return null;
	}
	
	
	public static T getResponse(HttpURLConnection con, Class<T> cl) {
		if(con!=null){
			
			try {
				
			   BufferedReader br = 
				new BufferedReader(
					new InputStreamReader(con.getInputStream()));
						
			   String input;
			   String output="";
						
			   while ((input = br.readLine()) != null){
				   output +=input;
			   }
			   br.close();
			   ObjectMapper mapper = new ObjectMapper();
			   return mapper.readValue(output.getBytes(), cl);
			  
						
			} catch (IOException e) {
			   e.printStackTrace();
			}
					
		       }
				
		   return null;
	}
	
	/**
	 * public AssessmentMapper findByEmailAndTestNameAndPaths( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2,@Param("path3") String path3,@Param("path4") String path4,@Param("path5") String path5, @Param("companyId") String companyId);

	public List<AssessmentMapper> findAssessmentsForUserByPath1( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2, @Param("companyId") String companyId);


	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("companyId") String companyId);


	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("companyId") String companyId);

	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("path5") String path5, @Param("companyId") String companyId);

	public AssessmentMapper saveOrUpdate(AssessmentMapper assessmentMapper);
	
	
	public AssessmentTraversalPath computeForUser(String email, String companyId);
	
	 */
	
	@RequestMapping(value = "/findByEmailAndTestNameAndPaths", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findByEmailAndTestNameAndPaths(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String testName,
			 @RequestParam String path1,
			 @RequestParam(required = false) String path2,
			 @RequestParam(required = false)  String path3,
			 @RequestParam(required = false)  String path4,
			 @RequestParam(required = false)  String path5
			)
			throws Exception {
		AssessmentMapper assessmentMapper =  assessmentMapperService.findByEmailAndTestNameAndPaths(email, testName, path1, path2, path3, path4, path5, companyId);
		return ResponseEntity.ok(assessmentMapper);
	}
	
	@RequestMapping(value = "/findAssessmentsForUserByPath1", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findAssessmentsForUserByPath1(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String path1
			)
			throws Exception {
		List<AssessmentMapper> assessmentMappers =  assessmentMapperService.findAssessmentsForUserByPath1(email, path1, companyId);
		
		 String eAssessToken = getTokenFromEAssess();
		 	for(AssessmentMapper ass: assessmentMappers) {
		 		
		 		String status = getTestStatusForUser(companyId, email, ass.getTestName(), eAssessToken);
		 		ass.setTestGivenByUser(status);
		 	}
		return ResponseEntity.ok(assessmentMappers);
	}
	
	@RequestMapping(value = "/findAssessmentsForUserByPath1AndPath2", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findAssessmentsForUserByPath1AndPath2(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String path1,
			 @RequestParam String path2
			)
			throws Exception {
		List<AssessmentMapper> assessmentMappers =  assessmentMapperService.findAssessmentsForUserByPath1AndPath2(email, path1, path2, companyId);
		String eAssessToken = getTokenFromEAssess();
	 	for(AssessmentMapper ass: assessmentMappers) {
	 		
	 		String status = getTestStatusForUser(companyId, email, ass.getTestName(), eAssessToken);
	 		ass.setTestGivenByUser(status);
	 	}
		return ResponseEntity.ok(assessmentMappers);
	}
	
	@RequestMapping(value = "/findAssessmentsForUserByPath1AndPath2AndPath3", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findAssessmentsForUserByPath1AndPath2AndPath3(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String path1,
			 @RequestParam String path2,
			 @RequestParam String path3
			)
			throws Exception {
		List<AssessmentMapper> assessmentMappers =  assessmentMapperService.findAssessmentsForUserByPath1AndPath2AndPath3(email, path1, path2, path3, companyId);
		
		return ResponseEntity.ok(assessmentMappers);
	}
	
	@RequestMapping(value = "/findAssessmentsForUserByPath1AndPath2AndPath3AndPath4", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String path1,
			 @RequestParam String path2,
			 @RequestParam String path3,
			 @RequestParam String path4
			)
			throws Exception {
		List<AssessmentMapper> assessmentMappers =  assessmentMapperService.findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(email, path1, path2, path3, path4, companyId);
		
		return ResponseEntity.ok(assessmentMappers);
	}
	
	@RequestMapping(value = "/findAssessmentsForUserByPath1AndPath2AndPath3AndPath4AndPath5", method = RequestMethod.GET)
	@CrossOrigin
	public ResponseEntity<?> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4AndPath5(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testId, 
			@RequestParam String email,
			 @RequestParam String path1,
			 @RequestParam String path2,
			 @RequestParam String path3,
			 @RequestParam String path4,
			 @RequestParam String path5
			)
			throws Exception {
		List<AssessmentMapper> assessmentMappers =  assessmentMapperService.findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(email, path1, path2, path3, path4, path5, companyId);
		return ResponseEntity.ok(assessmentMappers);
	}
	
	@RequestMapping(value = "/computeForUser", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> computeForUser(@RequestParam String token, @RequestParam String companyId, 
			@RequestParam String email){
		AssessmentTraversalPath path =  assessmentMapperService.computeForUser(email, companyId);
		return ResponseEntity.ok(path);
	}
	
	@RequestMapping(value="assessmentsForUser",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> assessmentsForUser( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String email,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<AssessmentMapper> assessments = assessmentMapperService.findAssessmentsForUser(companyId, email, PageRequest.of(pageNumber, 20));
	 List<AssessmentMapper> tests = assessments.getContent();
	 Map<String, List<AssessmentMapper>> map = groupByTestName(tests);
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(assessments.getNumber());
	 res.setRecordsTo(assessments.getNumberOfElements());
	 res.setTotalNumberOfPages(assessments.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 //res.setList(assessments.getContent());
	 String eAssessToken = getTokenFromEAssess();
	 	for(String key: map.keySet()) {
	 		List<AssessmentMapper> list = map.get(key);
	 		String status = getTestStatusForUser(companyId, email, key, eAssessToken);
	 			for(AssessmentMapper mapper : list) {
	 				mapper.setTestGivenByUser(status);
	 			}
	 	}
	 res.setMap(map);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="assessmentsForUser2",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> assessmentsForUser2( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String email,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<AssessmentMapper> assessments = assessmentMapperService.findAssessmentsForUser(companyId, email, PageRequest.of(pageNumber, 20));
	 List<AssessmentMapper> tests = assessments.getContent();
	// Map<String, List<AssessmentMapper>> map = groupByTestName(tests);
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(assessments.getNumber());
	 res.setRecordsTo(assessments.getNumberOfElements());
	 res.setTotalNumberOfPages(assessments.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 //res.setList(assessments.getContent());
	 String eAssessToken = getTokenFromEAssess();
	 	for(AssessmentMapper ass: tests) {
	 		
	 		String status = getTestStatusForUser(companyId, email, ass.getTestName(), eAssessToken);
	 		ass.setTestGivenByUser(status);
	 	}
	 //res.setMap(map);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="assessmentsForUser360DegreeReiew",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> assessmentsForUser360DegreeReiew( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String email,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<AssessmentMapper> assessments = assessmentMapperService.findAssessmentsForUser(companyId, email, PageRequest.of(pageNumber, 20));
	 List<AssessmentMapper> tests = assessments.getContent();
	 List<AssessmentMapper> retList = new ArrayList<>();
	// Map<String, List<AssessmentMapper>> map = groupByTestName(tests);
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(assessments.getNumber());
	 res.setRecordsTo(assessments.getNumberOfElements());
	 res.setTotalNumberOfPages(assessments.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 //res.setList(assessments.getContent());
	 String eAssessToken = getTokenFromEAssess();
	 	for(AssessmentMapper ass: tests) {
	 		if(ass.getReviewMode() != null && ass.getReviewMode()) {
	 			String url = ass.getTestLink();
	 			String assAppUserEmail = URLDecoder.decode( url.substring(url.indexOf("userId=")+7, url.indexOf("&companyId=")) ) ;
	 			System.out.println("assAppUserEmail is "+assAppUserEmail);
	 			String status = getTestStatusForUser(companyId, assAppUserEmail, ass.getTestName(), eAssessToken);
		 		ass.setTestGivenByUser(status);
	 		}
	 		
	 	}
	 //res.setMap(map);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
	}
	
	public String getTestStatusForUser(String companyId, String email, String testName, String token) throws MalformedURLException {
		String u = eAssessCheckTestStatusForUser;
		//token=${TOKEN}&companyId=${COMPANY_ID}&testName=${TEST_NAME}+ "&userId=${EMAIL}"
		
		u = u.replace("$[TOKEN]", URLEncoder.encode(token));
		u = u.replace("$[TEST_NAME]", URLEncoder.encode(testName));
		u = u.replace("$[EMAIL]", URLEncoder.encode(email));
		u = u.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
		System.out.println(" test status url "+ u);
		URL url2 = new URL(u);
		String testStatus = getStringResponseFromAPI(url2);
		return testStatus;
	}
	
	private Map<String, List<AssessmentMapper>> groupByTestName(List<AssessmentMapper> tests){
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
			for(AssessmentMapper ass : tests) {
				if(map.get(ass.getTestName()) == null) {
					List<AssessmentMapper> related = new ArrayList<AssessmentMapper>();
					related.add(ass);
					map.put(ass.getTestName(), related);
				}
				else {
					map.get(ass.getTestName()).add(ass);
				}
			}
			return map;
	}

}
