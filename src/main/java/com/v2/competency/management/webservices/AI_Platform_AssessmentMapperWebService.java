package com.v2.competency.management.webservices;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserSessionService;
import com.v2.competency.management.service.impl.PropertyConfig;
@RestController
public class AI_Platform_AssessmentMapperWebService {
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	VFTestService vfTestService; 
	
	@Autowired
	VFTestUserSessionService testUserSessionService;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	
	@RequestMapping(value = "/assignAssessmentToUserInAIPlatform", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> assignAssessmentToUser( @RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testIdentifier, 
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			 @RequestParam String testName,
			 @RequestParam Boolean external,
			 @RequestParam String path1,
			 @RequestParam(required = false) String path2,
			 @RequestParam(required = false)  String path3
			)
			throws Exception {
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
			if(tenant == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Company Id"+companyId);
			}
			
		VFTest test =  vfTestService.findByTestIdentifier(testIdentifier, companyId);
			if(test == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Test Id"+testIdentifier);
			}
		
		String url = config.getAitestPrivateUrl();
	
		
		
		url = url.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
		url = url.replace("$[TEST_IDENTIFIER]", URLEncoder.encode(testIdentifier));
		url = url.replace("$[EMAIL]", URLEncoder.encode(email));
	
		url = url.replace("$[FIRST_NAME]", URLEncoder.encode(firstName));
		url = url.replace("$[LAST_NAME]", URLEncoder.encode(lastName));
	
		String testLink = url;
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
				.testIdentifier(test.getTestIdentifier())
				.external(external)
				.path1(path1)
				.path2(path2==null?"NA":path2)
				.path3(path3==null?"NA":path3)
				.path4("NA")
				.path5("NA")
				.typePath1("NA")
				.typePath2("NA")
				.typePath3("NA")
				.typePath4("NA")
				.typePath5("NA")
				.build();
		
		assessmentMapper.setPath3(testName);
		assessmentMapper.setPath4("AI Platform");
		
		
		assessmentMapper.setCompanyId(companyId);
		assessmentMapper.setCompanyName(tenant.getCompanyName());
		assessmentMapperService.saveOrUpdate(assessmentMapper);
		
		return ResponseEntity.ok(testLink);

	}
	
	@RequestMapping(value="assessmentsForUser3",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> assessmentsForUser3( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String email,
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
	
	 	for(AssessmentMapper ass: tests) {
	 		VFTestUserSession sess =  testUserSessionService.finfVFTestUserSessionByEmail(email, companyId, ass.getTestIdentifier(), 1);
	 			if(sess != null) {
	 				ass.setTestGivenByUser("yes");
	 			}
	 			else {
	 				ass.setTestGivenByUser("no");
	 			}
	 		
	 		
	 	}
	 //res.setMap(map);
	 res.setList(tests);
	 return ResponseEntity.ok(res);
	}
	
	
	@RequestMapping(value = "/assignRolePlayToUser", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> assignRolePlayToUser( @RequestParam String token, @RequestParam String companyId, 
			@RequestParam(required = false) String testIdentifier, 
			@RequestParam String email,
			@RequestParam String firstName,
			@RequestParam String lastName,
			 @RequestParam String testName,
			 @RequestParam Boolean external)
			throws Exception {
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
			if(tenant == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Company Id"+companyId);
			}
			
		VFRolePlayTest test =  rolePlayTestService.findRolePlayTestsByTestName(companyId, testName);
			if(test == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Test Id"+testIdentifier);
			}
		
		String url = config.getAitestPrivateUrl();
	
		
		
		url = url.replace("$[COMPANY_ID]", URLEncoder.encode(companyId));
		url = url.replace("$[TEST_IDENTIFIER]", URLEncoder.encode(testIdentifier));
		url = url.replace("$[EMAIL]", URLEncoder.encode(email));
	
		url = url.replace("$[FIRST_NAME]", URLEncoder.encode(firstName));
		url = url.replace("$[LAST_NAME]", URLEncoder.encode(lastName));
	
		String testLink = url;
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
				.testIdentifier(test.getTestName())
				.external(external)
				.path1("NA")
				.path2("NA")
				.path3("NA")
				.path4("NA")
				.path5("NA")
				.typePath1("NA")
				.typePath2("NA")
				.typePath3("NA")
				.typePath4("NA")
				.typePath5("NA")
				.build();
		
		assessmentMapper.setPath3(testName);
		assessmentMapper.setPath4("AI Platform");
		
		
		assessmentMapper.setCompanyId(companyId);
		assessmentMapper.setCompanyName(tenant.getCompanyName());
		assessmentMapperService.saveOrUpdate(assessmentMapper);
		
		return ResponseEntity.ok(testLink);

	}
	
	
	@RequestMapping(value = "/assignMultipleRolePlayToMultipleUser", method = RequestMethod.POST)
	@CrossOrigin
	public ResponseEntity<?> assignMultipleRolePlayToMultipleUser(@RequestParam String token,
	                                                              @RequestParam String companyId,
	                                                              @RequestParam String emails,    
	                                                              @RequestParam String testNames,
	                                                              @RequestParam String managerEmail
	                                                              )
	        throws Exception {

	    Tenant tenant = tenantService.findTenantByCompanyId(companyId);
	    if (tenant == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Company Id " + companyId);
	    }

	    List<String> emailList = Arrays.stream(emails.split(","))
	            .map(String::trim)
	            .filter(s -> !s.isEmpty())
	            .collect(Collectors.toList());

	    List<String> testNameList = Arrays.stream(testNames.split("###"))
	            .map(String::trim)
	            .filter(s -> !s.isEmpty())
	            .collect(Collectors.toList());

	    if (emailList.isEmpty() || testNameList.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Emails or TestNames cannot be empty");
	    }

	    List<String> generatedLinks = new ArrayList<>();

	    for (String email : emailList) {
	        for (String testName : testNameList) {

	            VFRolePlayTest test = rolePlayTestService.findRolePlayTestsByTestName(companyId, testName.trim());
	            if (test == null) {
	                continue; // skip invalid test
	            }

	            String url = config.getAitestPrivateUrl();
	            url = url.replace("$[COMPANY_ID]", URLEncoder.encode(companyId, StandardCharsets.UTF_8));
	            url = url.replace("$[EMAIL]", URLEncoder.encode(email.trim(), StandardCharsets.UTF_8));

	            String testLink = url;
	            generatedLinks.add(testLink);

	            AssessmentMapper assessmentMapper = AssessmentMapper.builder()
	                    .email(email)
	                    .firstName("NA")
	                    .lastName("NA")
	                    .testLink(testLink)
	                    .testName(testName)
	                    .testIdentifier(test.getTestName())
	                    .assignedBy(managerEmail)
	                    .external(false)
	                    .path1("NA")
	                    .path2("NA")
	                    .path3("NA")
	                    .path4("NA")
	                    .path5("NA")
	                    .typePath1("NA")
	                    .typePath2("NA")
	                    .typePath3("NA")
	                    .typePath4("NA")
	                    .typePath5("NA")
	                    .aiPersonaType(test.getAiPersona())
	                    .build();

	            assessmentMapper.setCompanyId(companyId);
	            assessmentMapper.setCompanyName(tenant.getCompanyName());
	            assessmentMapperService.saveOrUpdate(assessmentMapper);
	        }
	    }

	    return ResponseEntity.ok(generatedLinks);
	}
	
	@GetMapping("/get tests assigned by a manager")
    public ResponseEntity<PaginatedResponseDto> getTestsAssignedByManager(
            @RequestParam String assignedBy,
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam String token) {

        PaginatedResponseDto response =
        		assessmentMapperService.getTestsAssignedByManager(assignedBy, companyId, page, size, sort);

        return ResponseEntity.ok(response);
    }
	

}
