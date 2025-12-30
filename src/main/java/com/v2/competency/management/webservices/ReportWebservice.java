package com.v2.competency.management.webservices;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v2.competency.management.dtos.AssessmentAssignmentTypeDto;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.Survey360DegreeScoreDto;
import com.v2.competency.management.dtos.UserTestSessionDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
public class ReportWebservice {
	
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PropertyConfig config;
	
	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	AssessmentMapperWebService assessmentMapperWebService;
	
	@Autowired
	VFTestService testService;
	
	@PostConstruct
	public void init() {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
	}
	
	@RequestMapping(value = "/computeAggregatedScoreForUser", method = RequestMethod.GET)
	public ResponseEntity<?> computeAggregatedScoreForUser( @RequestParam String token, @RequestParam String companyId){
		int page = 0;
		PaginatedResponseDto res = (PaginatedResponseDto) fetchUserlistingForReports(page, null, companyId).getBody();
		//page++;
		calculateAndUpdateScores((List<User>)res.getList());
		while(page < res.getTotalNumberOfPages()) {
			res = (PaginatedResponseDto) fetchUserlistingForReports(page, null, companyId).getBody();
			calculateAndUpdateScores((List<User>)res.getList());
			page++;
		}
		return ResponseEntity.ok("success");
	}
	
	private void calculateAndUpdateScores(List<User> users) {
		for(User user : users) {
			ResponseEntity resRoles = findRolesForUserAssignedAssessments(user.getEmail(), null, user.getCompanyId());
			List<AssessmentAssignmentTypeDto> forRoles = (List<AssessmentAssignmentTypeDto>) resRoles.getBody();
			saveScores(forRoles);
			
			
			ResponseEntity resCompetencies = findCompetenciesForUserAssignedAssessments(user.getEmail(), null, user.getCompanyId());
			List<AssessmentAssignmentTypeDto> forCompetencies = (List<AssessmentAssignmentTypeDto>) resCompetencies.getBody();
			saveScores(forCompetencies);
			
			ResponseEntity resJobDescs = findJobDescriptionsForUserAssignedAssessments(user.getEmail(), null, user.getCompanyId());
			List<AssessmentAssignmentTypeDto> forJobDescs = (List<AssessmentAssignmentTypeDto>) resJobDescs.getBody();
			saveScores(forJobDescs);
			
			ResponseEntity resConsolidatedAssessments = findConsolidatedAssessmentForUser(user.getEmail(), null, user.getCompanyId());
			List<AssessmentAssignmentTypeDto> forConsolidatedAssessments = (List<AssessmentAssignmentTypeDto>) resConsolidatedAssessments.getBody();
			saveScores(forConsolidatedAssessments);
			
			
			List<AssessmentAssignmentTypeDto> combinedListatUserLevel = new ArrayList<>();
			combinedListatUserLevel.addAll(forRoles);
			
			combinedListatUserLevel.addAll(forCompetencies);
			
			combinedListatUserLevel.addAll(forJobDescs);
			
			combinedListatUserLevel.addAll(forConsolidatedAssessments);
			
			Float average = calculateAverageScoreForUser(combinedListatUserLevel);
			user.setOverAllScore(average);
				if(user.getOverAllScore() != null) {
					user.setRoleOrDesig(user.getOrgHierarchy().getRoleOrDesig());
					userService.saveOrUpdate(user);
				}
		}
	}
	
	private Float calculateAverageScoreForUser(List<AssessmentAssignmentTypeDto> list) {
		Float score = 0f;
		Integer count = 0;
		for(AssessmentAssignmentTypeDto dto : list) {
			if(dto.getAverageScore() != null) {
				score+=dto.getAverageScore();
				count++;
			}
		}
		
		if(count > 0) {
			return score / count;
		}
		
		return null;
	}
	
	private void saveScores(List<AssessmentAssignmentTypeDto> assignments) {
		String apiResponse =  "";
		String testName ="";
		String email = "";
		try {
			
			for(AssessmentAssignmentTypeDto dto : assignments) {
				List<AssessmentMapper> mappers =  dto.getList();
				Integer count = 0;
				Float score = 0f;
				for(AssessmentMapper mapper : mappers) {
					testName = mapper.getTestName();
					email = mapper.getEmail();
					String companyId = mapper.getCompanyId();
					String userTestSessionAPI = config.getTestResultApi();
					userTestSessionAPI = userTestSessionAPI.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
					userTestSessionAPI = userTestSessionAPI.replace("$[TEST_NAME]", URLEncoder.encode(testName));
					userTestSessionAPI = userTestSessionAPI.replace("$[EMAIL]", URLEncoder.encode(email));
					apiResponse =  AssessmentMapperWebService.getStringResponseFromAPI(new URL(userTestSessionAPI));
					//getStringResponseFromAPI
						//if(!testName.toUpperCase().startsWith("FEEDBACK")) {
							if(apiResponse != null && apiResponse.trim().length() > 0) {
								UserTestSessionDto summary =  objectMapper.readValue(apiResponse.getBytes(), UserTestSessionDto.class);
								if(summary != null) {
									mapper.setScorePercent(summary.getWeightedScorePercentage());
									
									if(mapper.getScorePercent() != null) {
										System.out.println("Saving test score for "+mapper.getTestName()+" for "+mapper.getEmail());
										assessmentMapperService.saveOrUpdate(mapper);
										score += mapper.getScorePercent();
										count++;
									}
								}
							}
							
						//}
					
					
				}
				if(count > 0) {
					Float averageScorePerAssignment = score / count;
					dto.setAverageScore(averageScorePerAssignment);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("test "+testName+" email "+email);
			System.out.println("apiresponse "+apiResponse);
			e.printStackTrace();
			throw new RuntimeException("Problem in calling eAssess api");
		} 
	}
	
	
	
	@RequestMapping(value = "/fetchUserlistingForReports", method = RequestMethod.GET)
	public ResponseEntity<?> fetchUserlistingForReports(@RequestParam(name= "page", required = false) Integer pageNumber,  @RequestParam String token, @RequestParam String companyId){
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<String> users = assessmentMapperService.getListOfUsersAssignedTests(companyId, PageRequest.of(pageNumber, 15));
		List<User> usrs = new ArrayList<>();
		for(String usr : users) {
			usrs.add(userService.findByEmail(usr, companyId));
		}
		
		 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(users.getNumber());
		 res.setRecordsTo(users.getNumberOfElements());
		 res.setTotalNumberOfPages(users.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
		 res.setList(usrs);
		 return ResponseEntity.ok(res);
	}
	
	//public List<AssessmentMapper> findRolesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
//	public List<AssessmentMapper> findCompetenciesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	//public List<AssessmentMapper> findJobDescriptionsForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	//public List<AssessmentMapper> find360DegreeSurveysForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@RequestMapping(value = "/findRolesForUserAssignedAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> findRolesForUserAssignedAssessments(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.findRolesForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("Role")
					.valueOfAssignment(key)
					.list(map.get(key))
					.averageScore(calculateAverageScore(map.get(key)))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	@RequestMapping(value = "/findCompetenciesForUserAssignedAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> findCompetenciesForUserAssignedAssessments(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.findCompetenciesForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			System.out.println("type path 1 "+ass.getTypePath1()+" type path 2 "+ass.getTypePath2() +" type path 3"+ass.getTypePath3());
			System.out.println(" path 1 "+ass.getPath1()+"  path 2 "+ass.getPath2() +"  path 3"+ass.getPath3());
			
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			System.out.println("key is "+key);
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("Competency")
					.valueOfAssignment(key)
					.list(map.get(key))
					.averageScore(calculateAverageScore(map.get(key)))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	@RequestMapping(value = "/findJobDescriptionsForUserAssignedAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> findJobDescriptionsForUserAssignedAssessments(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.findJobDescriptionsForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("Job Description")
					.valueOfAssignment(key)
					.list(map.get(key))
					.averageScore(calculateAverageScore(map.get(key)))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	@RequestMapping(value = "/findConsolidatedAssessmentForUser", method = RequestMethod.GET)
	public ResponseEntity<?> findConsolidatedAssessmentForUser(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.findConsolidatedAssessmentsForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("Consolidated Assessment")
					.valueOfAssignment(key)
					.list(map.get(key))
					.averageScore(calculateAverageScore(map.get(key)))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	Float calculateAverageScore(List<AssessmentMapper> list) {
		Float totalScore = 0f;
		Integer count = 0;
		for(AssessmentMapper test : list) {
			if(test.getScorePercent() != null) {
				count++;
				totalScore += test.getScorePercent();
			}
		}
		if(count > 0) {
			Float average = totalScore / count;
			return average;
			//return decimalFormat.format(average);
		}
		else {
			return null;
		}
		
	}
	
	@RequestMapping(value = "/find360DegreeSurveysForUserAssignedAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> find360DegreeSurveysForUserAssignedAssessments(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.find360DegreeSurveysForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("360 Degree Survey")
					.valueOfAssignment(key)
					.list(map.get(key))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	@RequestMapping(value = "/findConsolidated360DegreeSurveysForUserAssignedAssessments", method = RequestMethod.GET)
	public ResponseEntity<?> findConsolidated360DegreeSurveysForUserAssignedAssessments(@RequestParam String email,  @RequestParam String token, @RequestParam String companyId){
		List<AssessmentMapper> list = assessmentMapperService.findConsolidated360DegreeSurveyForUserAssignedAssessments(email, companyId);
		Map<String, List<AssessmentMapper>> map = new HashMap<>();
		
		for(AssessmentMapper ass : list) {
			if(map.get(ass.getTypePath2()) == null) {
				List<AssessmentMapper> lt = new ArrayList<>();
				lt.add(ass);
				map.put(ass.getTypePath2(), lt);
			}
			else {
				map.get(ass.getTypePath2()).add(ass);
			}
		}
		
		List<AssessmentAssignmentTypeDto> ret = new ArrayList<>();
		for(String key : map.keySet()) {
			AssessmentAssignmentTypeDto dto = AssessmentAssignmentTypeDto.builder()
					.typeOfAssignment("Consolidated 360 Degree Survey")
					.valueOfAssignment(key)
					.list(map.get(key))
					.build();
			ret.add(dto);
					
		}
		return ResponseEntity.ok(ret);
	}
	
	@RequestMapping(value = "/find360DegreeSurveyScoreForUser", method = RequestMethod.GET)
	public ResponseEntity<?> find360DegreeSurveyScoreForUser(@RequestParam String email, @RequestParam String testName, @RequestParam String path1, @RequestParam String path2, @RequestParam(required = false) String path3, @RequestParam(required = false) String path4, @RequestParam(required = false) String path5,  @RequestParam String token, @RequestParam String companyId) throws JsonParseException, JsonMappingException, IOException{
		if(path3 == null) {
			path3 = "";
		}
		if(path4 == null) {
			path4 = "";
		}
		if(path5==null) {
			path5="";
		}
		
		AssessmentMapper self = assessmentMapperService.findByEmailAndTestNameAndPaths(email, testName, path1, path2, path3, path4, path5, companyId);
		List<AssessmentMapper> reviewers = assessmentMapperService.findConsolidatedReviewers(testName, email, path2, companyId);//path2 and typePath2 and same values
		String eAssessToken = AssessmentMapperWebService.getTokenFromEAssess();
			for(AssessmentMapper reviewer : reviewers) {
				if(reviewer.getScorePercent() == null) {
					String userTestSessionAPI = config.getTestResultApi();
					userTestSessionAPI = userTestSessionAPI.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
					userTestSessionAPI = userTestSessionAPI.replace("$[TEST_NAME]", URLEncoder.encode(testName));
					User user = userService.findByEmail(email, companyId);
					String emailConstructed = reviewer.getEmail()+"$$$"+testName+"$$$"+email+"$$$"+user.getFirstName()+"$$$"+user.getLastName();
					
					
					String status = assessmentMapperWebService.getTestStatusForUser(companyId, emailConstructed, testName, eAssessToken);
					if(status != null && status.equalsIgnoreCase("Yes")) {
						userTestSessionAPI = userTestSessionAPI.replace("$[EMAIL]", URLEncoder.encode(emailConstructed));
						System.out.println("emailConstructed "+emailConstructed+" . userTestSessionAPI "+userTestSessionAPI);
						String apiResponse =  AssessmentMapperWebService.getStringResponseFromAPI(new URL(userTestSessionAPI));
						if(apiResponse != null && apiResponse.trim().length() > 0) {
							UserTestSessionDto summary =  objectMapper.readValue(apiResponse.getBytes(), UserTestSessionDto.class);
							if(summary != null) {
								reviewer.setScorePercent(summary.getWeightedScorePercentage());
								System.out.println("Saving test score for "+reviewer.getTestName()+" for "+reviewer.getEmail());
								assessmentMapperService.saveOrUpdate(reviewer);
								
							}
						}
					}
					
					
					
				}
			}
		Survey360DegreeScoreDto ret = new Survey360DegreeScoreDto();
		ret.setSelf(self);
		ret.setReviewers(reviewers);
		Float total = (self.getScorePercent()==null?0f:self.getScorePercent());
		for(AssessmentMapper reviewer : reviewers) {
			total += (reviewer.getScorePercent()==null?0f:reviewer.getScorePercent());
		}
		Float average = total / (reviewers.size() + 1);
		ret.setAverageScore(average);
		return ResponseEntity.ok(ret);
	}
	
	
	

}
