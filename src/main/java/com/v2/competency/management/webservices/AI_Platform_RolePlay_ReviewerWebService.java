package com.v2.competency.management.webservices;

import java.util.List;

import javax.servlet.http.HttpSession;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.RolePlayQuestionAnswer;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;
import com.v2.competency.management.service.SyncAIInsightsGenService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;

@RestController
public class AI_Platform_RolePlay_ReviewerWebService {
	
	@Autowired
	SyncAIInsightsGenService aiInsightsGenService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	@Autowired
	RolePlayQuestionAnswerService answerService;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value="getUsersForRolePlayAssessments",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> getUsersForAssessments( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String testName, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFRolePlayTestSession> sessions =  rolePlayTestSessionService.findUserSessionsForTest(testName, companyId, PageRequest.of(pageNumber, 30));
	 List<VFRolePlayTestSession> list = sessions.getContent();
	 		

	 	 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(sessions.getNumber());
		 res.setRecordsTo(sessions.getNumberOfElements());
		 res.setTotalNumberOfPages(sessions.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="getSCIUserRolePlayAssessments",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> getSCIUserRolePlayAssessments(  @RequestParam String companyId, @RequestParam String testName, @RequestParam String sciEmailDomain, 
           HttpSession session, @RequestParam String token) throws Exception{  
	 List<VFRolePlayTestSession> list = rolePlayTestSessionService.seatchEmailUserSessionsForTest(testName, companyId, sciEmailDomain);
	 return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="fetchAllAnswersForRolePlayAssessment",method=RequestMethod.GET)  
	@CrossOrigin
	public ResponseEntity<?> fetchAllAnswersForRolePlayAssessment(  @RequestParam String companyId, @RequestParam Long rolePlayTestSessionId, 
           HttpSession session, @RequestParam String token) throws Exception{  
		VFRolePlayTestSession sess = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSessionId);
		List<RolePlayQuestionAnswer> allQA = answerService.findAllQAForUser(sess.getTestName(), sess.getEmail(), companyId, sess.getAttempt());
	 return ResponseEntity.ok(allQA);
	}
	
	
	@RequestMapping(value="manuallyGenerateAIInsightsFoRolePlayassessment",method=RequestMethod.POST)  
	@CrossOrigin
	public ResponseEntity<?> manuallyGenerateAIInsightsFoRolePlayassessment( @RequestParam String companyId, @RequestParam Long rolePlayTestSessionId, 
           HttpSession session, @RequestParam String token) throws Exception{  
		VFRolePlayTestSession sess = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSessionId);
		List<RolePlayQuestionAnswer> allQA = answerService.findAllQAForUser(sess.getTestName(), sess.getEmail(), companyId, sess.getAttempt());
        StringBuilder transcript = new StringBuilder();

        
        for (RolePlayQuestionAnswer qa : allQA) {
            transcript.append("Q: ").append(qa.getQuestion()).append("\n");
            transcript.append("A: ").append(qa.getAnswer()).append("\n");
        }
		
		
		try {
			aiInsightsGenService.generateInsightsForRolePlayBasedAssessmentSync(sess.getTestName(), sess.getEmail(), companyId, sess, transcript.toString());
			return ResponseEntity.ok("OK");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("in manuallyGenerateAIInsightsFoRolePlayassessment error in gen analysos "+e.getMessage());
			
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
					
		}
	 
	}
	
	@RequestMapping(value="postReviewerCommentsOnRolePlayAssessment",method=RequestMethod.POST)  
	@CrossOrigin
	public ResponseEntity<?> postReviewerCommentsOnRolePlayAssessment( @RequestParam String companyId, @RequestParam Long rolePlayTestSessionId, @RequestBody RolePlayInsightsDto insights,
           HttpSession session, @RequestParam String token) throws Exception{  
		VFRolePlayTestSession sess = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSessionId);
		String reviewerJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insights);
		sess.setReviewerInsightsJson(reviewerJson);
		sess.setReviewerInsightsDto(insights);
		sess.setReviewDone(true);
		rolePlayTestSessionService.saveOrUpdate(sess);
	 return ResponseEntity.ok("OK");
	}

}
