package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.dtos.WorkflowAssignmentDto;
import com.v2.competency.management.dtos.WorkflowAssignmentToUserStatusDesc;
import com.v2.competency.management.dtos.WorkflowNodeType;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.entities.VideoAgentSession;
import com.v2.competency.management.entities.WorkflowNode;
import com.v2.competency.management.entities.WorkflowNodeSession;
import com.v2.competency.management.entities.WorkflowRecommGenerator;
import com.v2.competency.management.entities.WorkflowSession;
import com.v2.competency.management.entities.WorkflowSessionStatus;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.VFRolePlayTestRepo;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.repos.WorkflowSessionRepo;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.service.VideoAgentSessionService;
import com.v2.competency.management.service.WorkflowNodeService;
import com.v2.competency.management.service.WorkflowNodeSessionService;
import com.v2.competency.management.service.WorkflowSessionService;

@RestController
@CrossOrigin
public class WorkflowUserController {
	
	@Autowired
	WorkflowSessionService workflowSessionService;
	
	@Autowired
	ZimulateWorkflowRepo workflowRepo;
	
	@Autowired
	WorkflowNodeService workflowNodeService;
	
	@Autowired
	VideoAgentRepo videoAgentRepo;
	
	@Autowired
	VideoAgentSessionService videoAgentSessionService;
	
	@Autowired
	WorkflowNodeSessionService workflowNodeSessionService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	@Autowired
	VFRolePlayTestRepo vfRolePlayTestRepo;
	
	@Autowired
	VFRolePlayTestSessionRepo rolePlayTestSessionRepo;
	
	@Autowired
	WorkflowRecommGenerator workflowRecommGenerator;
	
	@Autowired
	WorkflowSessionRepo workflowSessionRepo;
	
	@RequestMapping(value="assignWorkflowToUser",method=RequestMethod.POST) 
	@CrossOrigin
    public ResponseEntity<?> assignWorkflowToUser( @RequestBody WorkflowAssignmentDto assignments,  
           HttpSession sess, @RequestParam String token) throws Exception{  
		List<WorkflowAssignmentToUserStatusDesc> responses = new ArrayList<>();
		for(Long workflow : assignments.getWorkflowIds()) {
			for(String email : assignments.getEmails()) {
				WorkflowSession session = workflowSessionService.findUniqueWorkflowSession(workflow, email, assignments.getCompanyId());
				if(session != null) {
					WorkflowAssignmentToUserStatusDesc st = WorkflowAssignmentToUserStatusDesc.builder().assignmentDesc("Already assigned")
							.email(email)
							.workflowId(workflow)
							.build();
					
					responses.add(st);
				}
				else {
					session = new WorkflowSession();
					ZimulateWorkflow flow = workflowRepo.findById(workflow).get();
					session.setCompanyId(assignments.getCompanyId());
					session.setEmail(email);
					session.setWorkflow(flow);
					session.setStatus(WorkflowSessionStatus.NOT_STARTED.getStatus());
					workflowSessionService.saveOrUpdate(session);
					WorkflowAssignmentToUserStatusDesc st = WorkflowAssignmentToUserStatusDesc.builder().assignmentDesc("Assigned")
							.email(email)
							.workflowId(workflow)
							.build();
					
					responses.add(st);
				}
			}
		}
		
		return ResponseEntity.ok(responses);	
	}
	
	@RequestMapping(value="markVideoAgentCompletion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> markVideoAgentCompletion( @RequestParam String token, @RequestParam String companyId, @RequestParam String email, @RequestParam Long videoAgentId,  @RequestParam Integer position, @RequestParam Long workFlowId){
		ZimulateWorkflow workflow = workflowRepo.findById(workFlowId).get();
			if(workflow == null) {
				return ResponseEntity.badRequest().body("Workflow Id does not exist "+workFlowId);
			}
		WorkflowNode workflowNode = workflowNodeService.findUniqueWorkflowNode(position, workFlowId, companyId);
			if(!workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())) {
				return ResponseEntity.badRequest().body("No video Training Node on the given position "+position);
			}
		VideoAgent videoAgent = videoAgentRepo.findById(videoAgentId).get();
			if(videoAgent == null) {
				return ResponseEntity.badRequest().body("Video Agent  "+position);
			}
		
		WorkflowSession workflowSession =  workflowSessionService.findUniqueWorkflowSession(workFlowId, email, companyId);
			if(workflowSession == null) {
				return ResponseEntity.badRequest().body("Work flow Session does not exist. Contact Admin  ");
			}
			
		/**
		 * Step 1 - Save VideoAgentSession record
		 */
		VideoAgentSession session = VideoAgentSession.builder().email(email)
				.videoAgent(videoAgent)
				.build();
		session.setCompanyId(companyId);
		session = videoAgentSessionService.createSession(session);
		
		
		/**
		 * Step 2 - Create WorkflowNodeSession record with VideoAgentSession record embedded
		 */
		WorkflowNodeSession workflowNodeSession = WorkflowNodeSession.builder()
				.position(position)
				.nodeType(WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())
				.workflowId(workFlowId)
				.workflowNodeId(workflowNode.getId())
				.workflowNodeInstanceId(session.getId())
				.videoAgentSession(session)
				.workflowSessionId(workflowSession.getId())
				.email(email)
				.build();
		workflowNodeSession.setCompanyId(companyId);
		/**
		 * Step 3 - Create WorkflowNodeSession record.
		 */
		workflowNodeSessionService.markComplete(workflowNodeSession);
		
			if(workflowSession.getStatus().equalsIgnoreCase(WorkflowSessionStatus.NOT_STARTED.getStatus())) {
				/**
				 * Step 4 - Update Workflow Session with status flag if applicable
				 */
				workflowSession.setStatus(WorkflowSessionStatus.IN_PROGRESS.getStatus());
				workflowSessionService.saveOrUpdate(workflowSession);
			}
		
		return ResponseEntity.ok("ok");
	}
    

	@RequestMapping(value="markRolePlayTrainingNodeCompletion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> markRolePlayTrainingNodeCompletion( @RequestParam String token, @RequestParam String companyId, @RequestParam String email, @RequestParam Long rolePlayTestId,  @RequestParam Integer position, @RequestParam Long workFlowId){
		ZimulateWorkflow workflow = workflowRepo.findById(workFlowId).get();
			if(workflow == null) {
				return ResponseEntity.badRequest().body("Workflow Id does not exist "+workFlowId);
			}
		WorkflowNode workflowNode = workflowNodeService.findUniqueWorkflowNode(position, workFlowId, companyId);
			if(!workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType())) {
				return ResponseEntity.badRequest().body("No Roleplay Training Node on the given position "+position);
			}
			
		VFRolePlayTest rolePlayTestTraining = vfRolePlayTestRepo.findById(rolePlayTestId).get();
			if(rolePlayTestTraining == null) {
				return ResponseEntity.badRequest().body("No Role play test present");
			}
			
		WorkflowSession workflowSession =  workflowSessionService.findUniqueWorkflowSession(workFlowId, email, companyId);
			if(workflowSession == null) {
				return ResponseEntity.badRequest().body("Work flow Session does not exist. Contact Admin  ");
			}
			
			/**
			 * Step 1 - Save RolePlayTestSession record
			 */
		VFRolePlayTestSession rolePlayTestSession = new VFRolePlayTestSession();
		rolePlayTestSession.setCompanyId(companyId);
		rolePlayTestSession.setAttempt(1);
		rolePlayTestSession.setTestIdentifier(rolePlayTestTraining.getTestName());
		rolePlayTestSession.setTestName(rolePlayTestTraining.getTestName());
		rolePlayTestSession.setEmail(email);
		rolePlayTestSession = rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		
		/**
		 * Step 2 - Create WorkflowNodeSession record with VideoAgentSession record embedded
		 */
		WorkflowNodeSession workflowNodeSession = WorkflowNodeSession.builder()
				.position(position)
				.email(email)
				.nodeType(WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType())
				.workflowId(workFlowId)
				.workflowNodeId(workflowNode.getId())
				.workflowNodeInstanceId(rolePlayTestSession.getId())
				.rolePlayTrainingSession(rolePlayTestSession)
				.workflowSessionId(workflowSession.getId())
				.build();
		workflowNodeSession.setCompanyId(companyId);
		/**
		 * Step 3 - Create WorkflowNodeSession record.
		 */
		workflowNodeSessionService.markComplete(workflowNodeSession);
		
			if(workflowSession.getStatus().equalsIgnoreCase(WorkflowSessionStatus.NOT_STARTED.getStatus())) {
				/**
				 * Step 4 - Update Workflow Session with status flag if applicable
				 */
				workflowSession.setStatus(WorkflowSessionStatus.IN_PROGRESS.getStatus());
				workflowSessionService.saveOrUpdate(workflowSession);
			}
		
		return ResponseEntity.ok("ok");
		
	}
	
	@RequestMapping(value="markRolePlayAssessmentNodeCompletion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> markRolePlayAssessmentNodeCompletion( @RequestParam String token, @RequestParam String companyId, @RequestParam String email, 
    		@RequestParam Long rolePlayTestId,  @RequestParam Integer position, @RequestParam Long workFlowId, @RequestParam Integer assessmentAttempt){
		ZimulateWorkflow workflow = workflowRepo.findById(workFlowId).get();
			if(workflow == null) {
				return ResponseEntity.badRequest().body("Workflow Id does not exist "+workFlowId);
			}
		WorkflowNode workflowNode = workflowNodeService.findUniqueWorkflowNode(position, workFlowId, companyId);
			if(!workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())) {
				return ResponseEntity.badRequest().body("No Roleplay assessment Node on the given position "+position);
			}
			
		VFRolePlayTest rolePlayTestTraining = vfRolePlayTestRepo.findById(rolePlayTestId).get();
			if(rolePlayTestTraining == null) {
				return ResponseEntity.badRequest().body("No Role play test present");
			}
			
		WorkflowSession workflowSession =  workflowSessionService.findUniqueWorkflowSession(workFlowId, email, companyId);
			if(workflowSession == null) {
				return ResponseEntity.badRequest().body("Work flow Session does not exist. Contact Admin  ");
			}
			
			/**
			 * Step 1 - Save RolePlayTestSession record
			 */
		VFRolePlayTestSession rolePlayTestSession = new VFRolePlayTestSession();
		rolePlayTestSession.setCompanyId(companyId);
		rolePlayTestSession.setAttempt(assessmentAttempt);
		rolePlayTestSession.setTestIdentifier(rolePlayTestTraining.getTestName());
		rolePlayTestSession.setTestName(rolePlayTestTraining.getTestName());
		rolePlayTestSession.setEmail(email);
		rolePlayTestSession = rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		
		/**
		 * Step 2 - Create WorkflowNodeSession record with VideoAgentSession record embedded
		 */
		WorkflowNodeSession workflowNodeSession = WorkflowNodeSession.builder()
				.position(position)
				.email(email)
				.nodeType(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())
				.workflowId(workFlowId)
				.workflowNodeId(workflowNode.getId())
				.workflowNodeInstanceId(rolePlayTestSession.getId())
				.rolePlayAssessmentSession(rolePlayTestSession)
				.workflowSessionId(workflowSession.getId())
				.build();
		workflowNodeSession.setCompanyId(companyId);
		/**
		 * Step 3 - Create WorkflowNodeSession record.
		 */
		workflowNodeSessionService.markComplete(workflowNodeSession);
		
			if(workflowSession.getStatus().equalsIgnoreCase(WorkflowSessionStatus.NOT_STARTED.getStatus())) {
				/**
				 * Step 4 - Update Workflow Session with status flag if applicable
				 */
				workflowSession.setStatus(WorkflowSessionStatus.IN_PROGRESS.getStatus());
				workflowSessionService.saveOrUpdate(workflowSession);
			}
		
		return ResponseEntity.ok("ok");
		
	}
	
	@RequestMapping(value="markRolePlayShowResultsCompletion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> markRolePlayShowResultsCompletion( @RequestParam String token, @RequestParam String companyId, @RequestParam String email, @RequestParam Boolean showResults,  @RequestParam Integer position, @RequestParam Long workFlowId){
		ZimulateWorkflow workflow = workflowRepo.findById(workFlowId).get();
			if(workflow == null) {
				return ResponseEntity.badRequest().body("Workflow Id does not exist "+workFlowId);
			}
		WorkflowNode workflowNode = workflowNodeService.findUniqueWorkflowNode(position, workFlowId, companyId);
			if(!workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType())) {
				return ResponseEntity.badRequest().body("No Show Results Node on the given position "+position);
			}
			
		
			
		WorkflowSession workflowSession =  workflowSessionService.findUniqueWorkflowSession(workFlowId, email, companyId);
			if(workflowSession == null) {
				return ResponseEntity.badRequest().body("Work flow Session does not exist. Contact Admin  ");
			}
			
			
		
		/**
		 * Step 1 - Create WorkflowNodeSession record with VideoAgentSession record embedded
		 */
		WorkflowNodeSession workflowNodeSession = WorkflowNodeSession.builder()
				.position(position)
				.email(email)
				.nodeType(WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType())
				.workflowId(workFlowId)
				.workflowNodeId(workflowNode.getId())
				.resultsShown(true)
				.workflowSessionId(workflowSession.getId())
				.build();
		workflowNodeSession.setCompanyId(companyId);
		/**
		 * Step 3 - Create WorkflowNodeSession record.
		 */
		workflowNodeSessionService.markComplete(workflowNodeSession);
		
			if(workflowSession.getStatus().equalsIgnoreCase(WorkflowSessionStatus.NOT_STARTED.getStatus())) {
				/**
				 * Step 4 - Update Workflow Session with status flag if applicable
				 */
				workflowSession.setStatus(WorkflowSessionStatus.IN_PROGRESS.getStatus());
				workflowSessionService.saveOrUpdate(workflowSession);
			}
		
		return ResponseEntity.ok("ok");
		
	}
	
	@RequestMapping(value="markRolePlayShowRecommendationsCompletion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> markRolePlayShowRecommendationsCompletion( @RequestParam String token, @RequestParam String companyId, @RequestParam String email, @RequestParam Boolean showRecomm,  @RequestParam Integer position, @RequestParam Long workFlowId){
		ZimulateWorkflow workflow = workflowRepo.findById(workFlowId).get();
			if(workflow == null) {
				return ResponseEntity.badRequest().body("Workflow Id does not exist "+workFlowId);
			}
		WorkflowNode workflowNode = workflowNodeService.findUniqueWorkflowNode(position, workFlowId, companyId);
			if(!workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType())) {
				return ResponseEntity.badRequest().body("No  Training Recommendations Node on the given position "+position);
			}
			
		
			
		WorkflowSession workflowSession =  workflowSessionService.findUniqueWorkflowSession(workFlowId, email, companyId);
			if(workflowSession == null) {
				return ResponseEntity.badRequest().body("Work flow Session does not exist. Contact Admin  ");
			}
			
			
		
		/**
		 * Step 1 - Create WorkflowNodeSession record with VideoAgentSession record embedded
		 */
		WorkflowNodeSession workflowNodeSession = WorkflowNodeSession.builder()
				.position(position)
				.email(email)
				.nodeType(WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType())
				.workflowId(workFlowId)
				.workflowNodeId(workflowNode.getId())
				.resultsShown(false)
				.recommendationsShown(true)
				.workflowSessionId(workflowSession.getId())
				.build();
		workflowNodeSession.setCompanyId(companyId);
		/**
		 * Step 3 - Create WorkflowNodeSession record.
		 */
		workflowNodeSessionService.markComplete(workflowNodeSession);
		
			if(workflowSession.getStatus().equalsIgnoreCase(WorkflowSessionStatus.NOT_STARTED.getStatus())) {
				/**
				 * Step 4 - Update Workflow Session with status flag if applicable
				 */
				workflowSession.setStatus(WorkflowSessionStatus.COMPLETE.getStatus());
				workflowSessionService.saveOrUpdate(workflowSession);
			}
		
		return ResponseEntity.ok("ok");
		
	}
	
	@RequestMapping(value="fetchWorkflowsAssignedToUser",method=RequestMethod.GET) 
	@CrossOrigin
    public ResponseEntity<?> fetchWorkflowsAssignedToUser( @RequestParam String token, @RequestParam String companyId, 
    		@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size, 
	        @RequestParam String email){
		Pageable pageable = PageRequest.of(page, size);
		List<WorkflowSession> sessions =  workflowSessionService.findAllWorkflowSessionForUserByCompanyId(companyId, email, pageable).getContent();
		return ResponseEntity.ok(sessions);
	}
	
	@RequestMapping(value="fetchWorkflowsSessionById",method=RequestMethod.GET) 
	@CrossOrigin
    public ResponseEntity<?> fetchWorkflowsSessionById( @RequestParam String token, @RequestParam Long workflowSessionId){
		return ResponseEntity.ok(workflowSessionRepo.findById(workflowSessionId).get());
	}
	
	@RequestMapping(value="fetchAllWorkflowNodesToAssignedToUser",method=RequestMethod.GET) 
	@CrossOrigin
    public ResponseEntity<?> fetchAllWorkflowNodesToAssignedToUser( @RequestParam String token, @RequestParam String companyId, 
	        @RequestParam String email, @RequestParam Long workflowId, @RequestParam Long workflowSessionId){
		List<WorkflowNodeSession> nodeInstances =  workflowNodeSessionService.findAllWorkflowNodesByCompanyId(companyId, workflowId, workflowSessionId, email);
		return ResponseEntity.ok(nodeInstances);
	}
	
	@RequestMapping(value="testWorkflowInsightsGen",method=RequestMethod.GET) 
	@CrossOrigin
    public ResponseEntity<?> testWorkflowInsightsGen( @RequestParam String token, @RequestParam Long rolePlayTestSessionId) throws IOException{
		VFRolePlayTestSession session = rolePlayTestSessionRepo.findById(rolePlayTestSessionId).get();
		if(workflowRecommGenerator.checkIfRecommCanBeGenerated(session.getId(), session.getWorkflowSessionId())) {
        	String json = workflowRecommGenerator.generateRecommendationsSync(session.getWorkflowSessionId());
        	return ResponseEntity.ok(json);
        }
		return ResponseEntity.ok().build();
	}
	
}
