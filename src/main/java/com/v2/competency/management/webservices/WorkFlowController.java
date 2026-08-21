package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.WorkFlowDto;
import com.v2.competency.management.dtos.WorkflowNodeDto;
import com.v2.competency.management.dtos.WorkflowNodeType;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.entities.WorkflowNode;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.VFRolePlayTestRepo;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.repos.WorkflowNodeRepo;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.WorkflowNodeService;
import com.v2.competency.management.service.ZimulateWorkflowService;

@RestController
@CrossOrigin
public class WorkFlowController {
	@Autowired
	ZimulateWorkflowService workflowService;
	
	@Autowired
	ZimulateWorkflowRepo workflowRepo;
	
	@Autowired
	WorkflowNodeService workflowNodeService;
	
	@Autowired
	VideoAgentRepo videoAgentRepo;
	
	@Autowired
	VFRolePlayTestRepo rolePlayTestRepo;
	
	@Autowired
	WorkflowNodeRepo nodeRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	private List<WorkFlowDto> convert(List<ZimulateWorkflow> flows){
		List<WorkFlowDto> list = new ArrayList<>();
		for(ZimulateWorkflow flow : flows) {
			list.add(convert(flow));
		}
		return list;
	}
	
	private WorkFlowDto convert(ZimulateWorkflow flow) {
		WorkFlowDto dto = WorkFlowDto.builder().build();
		mapper.map(flow, dto);
		return dto;
	}
	
	private WorkflowNodeDto convert(WorkflowNode workflowNode) {
		WorkflowNodeDto dto = WorkflowNodeDto.builder().build();
		mapper.map(workflowNode, dto);
		return dto;
	}
	
	private List<WorkflowNodeDto> convertNodes(List<WorkflowNode> nodes){
		List<WorkflowNodeDto> list = new ArrayList<>();
		for(WorkflowNode node : nodes) {
			list.add(convert(node));
		}
		return list;
	}
	
	@RequestMapping(value = "/checkIfNameExists", method = RequestMethod.GET)
	public ResponseEntity<?> checkIfNameExists( @RequestParam String token, @RequestParam String companyId, @RequestParam String industry, @RequestParam String name){
	   if(workflowService.findUniqueZimulateWorkflow(name, industry, companyId) != null) {
		   return ResponseEntity.badRequest().body("Name already exists!");
	   }
	   
	   return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value = "/saveWorkFlow", method = RequestMethod.POST)
	public ResponseEntity<?> saveWorkFlow( @RequestParam String token, @RequestParam String companyId, @RequestBody WorkFlowDto workflow) throws IOException{
		ZimulateWorkflow flow = ZimulateWorkflow.builder().build();
		mapper.map(workflow, flow);
		flow.setCompanyId(companyId);
		return ResponseEntity.ok(convert(workflowService.saveOrUpdate(flow)));
	}
	
	@RequestMapping(value = "/findAllWorkflows", method = RequestMethod.GET)
	public ResponseEntity<PaginatedResponseDto> findAllWorkflows(
	        @RequestParam String token,
	        @RequestParam String companyId,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) throws IOException {

	    PaginatedResponseDto paginatedResponse = workflowService.findAllWorkflows(companyId, search, page, size);

	    return ResponseEntity.ok(paginatedResponse);
	}
	
	@GetMapping("/canUpdateOrDeleteWorkFlow")
    public ResponseEntity<Boolean> canUpdateOrDeleteWorkflow(

            @RequestParam Long workflowId,

            @RequestParam String companyId,

            @RequestParam String token) {

        return ResponseEntity.ok(
        		workflowService.canUpdateOrDeleteWorkflow(
                        workflowId,
                        companyId
                )
        );
    }


    @DeleteMapping("/workflow-delete")
    public ResponseEntity<?> deleteWorkflow(

            @RequestParam Long workflowId,

            @RequestParam String companyId,

            @RequestParam String token) {

    	workflowService.deleteWorkflow(
                workflowId,
                companyId
        );

        return ResponseEntity.ok(
                "Workflow deleted successfully"
        );
    }
	
	private WorkflowNode validateAndTransform(WorkflowNodeDto workflowNode) {
		WorkflowNode actual =   WorkflowNode.builder().build();
		mapper.map(workflowNode, actual);
		if(workflowNode.getType().equalsIgnoreCase( WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())) {
			if(workflowNode.getVideoAgentTrainingId() == null) {
				throw new RuntimeException("Video Agent (Training) can not be null");
			}
			else {
				if(workflowNode.getRolePlayAssessmentId() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayTrainingId() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults()!=null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations()!=null &&  workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
			}
			
		VideoAgent videoAgentTraining = videoAgentRepo.findById(workflowNode.getVideoAgentTrainingId()).get();
			if(videoAgentTraining == null) {
				throw new RuntimeException("Video Agent Id invalid");
			}
			actual.setVideoAgentTraining(videoAgentTraining);
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType())) {
			if(workflowNode.getRolePlayTrainingId() == null) {
				throw new RuntimeException("Role Play (Training) can not be null");
			}
			else {
				if(workflowNode.getRolePlayAssessmentId() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTrainingId() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults()!= null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations()!=null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
			}
		VFRolePlayTest rolePlayTestTraining = rolePlayTestRepo.findById(workflowNode.getRolePlayTrainingId()).get();	
			if(rolePlayTestTraining == null) {
				throw new RuntimeException("Role Play Test Training Id invalid");
			}
			actual.setRolePlayTraining(rolePlayTestTraining);
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())) {
			if(workflowNode.getRolePlayAssessmentId() == null) {
				throw new RuntimeException("Role Play (Assessment) can not be null");
			}
			else {
				if(workflowNode.getRolePlayTrainingId() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTrainingId() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults()!= null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations()!=null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
			}
			VFRolePlayTest rolePlayTestAssessment = rolePlayTestRepo.findById(workflowNode.getRolePlayAssessmentId()).get();
				if(rolePlayTestAssessment == null) {
					throw new RuntimeException("Role Play Test Assessment Id invalid");
				}
				actual.setRolePlayAssessment(rolePlayTestAssessment);
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType())) {
			if(workflowNode.getShowResults() == null || !workflowNode.getShowResults()) {
				throw new RuntimeException("Show Results flag can not be null or false");
			}
			else {
				if(workflowNode.getRolePlayTrainingId() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTrainingId() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayAssessmentId() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations()!=null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
			}
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType())) {
			if(workflowNode.getShowTrainingRecommendations() == null || !workflowNode.getShowTrainingRecommendations()) {
				throw new RuntimeException("Show Training Recommendations flag can not be null or false");
			}
			else {
				if(workflowNode.getRolePlayTrainingId() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTrainingId() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayAssessmentId() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults() != null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have 'Show Results' flag enabled if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
			}
		}
		return actual;
	}
	
	@RequestMapping(value = "/addOrUpdateNode", method = RequestMethod.POST)
	public ResponseEntity<?> addOrUpdateNode( @RequestParam String token, @RequestParam String companyId, 
			@RequestParam Integer position,  
			@RequestParam Long workflowId,  
			@RequestBody WorkflowNodeDto workflowNode) throws IOException{
		
		if(workflowRepo.findById(workflowId) == null) {
			return ResponseEntity.badRequest().body("Workflow with id "+workflowId+" does not exist");
		}
		
		WorkflowNode actual = validateAndTransform(workflowNode);
		actual.setCompanyId(companyId);
		try {
			return ResponseEntity.ok(workflowNodeService.saveOrUpdate(actual));
		} catch (RuntimeException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	
	@RequestMapping(value = "/deleteNode", method = RequestMethod.POST)
	public ResponseEntity<?> deleteNode( @RequestParam String token, @RequestParam String companyId, 
			@RequestParam Integer currentPosition,  
			@RequestParam Long nodeId,  
			@RequestParam Long workflowId  
			) throws IOException{
		
		if(workflowRepo.findById(workflowId) == null) {
			return ResponseEntity.badRequest().body("Workflow with id "+workflowId+" does not exist");
		}
		
		WorkflowNode node =  nodeRepo.findById(nodeId).get();
			if(node == null) {
				return ResponseEntity.badRequest().body("Node with id "+nodeId+" does not exist");
			}
			
			if(node.getPosition() != currentPosition) {
				return ResponseEntity.badRequest().body("Position value passed "+currentPosition+" does not match with the actual positon of the node "+node.getPosition());
			}
			
		nodeRepo.delete(node);
			
		List<WorkflowNode> nodesAfterCurentPosition = nodeRepo.findAllWorkflowNodesAfterPosition(companyId, workflowId, currentPosition);
			for(WorkflowNode nd : nodesAfterCurentPosition) {
				nd.setPosition(nd.getPosition() - 1);
				nodeRepo.save(nd);
			}
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value = "/findAllNodesInWorkflow", method = RequestMethod.GET)
	public ResponseEntity<?> findAllNodesInWorkflow( @RequestParam String token, @RequestParam String companyId, @RequestParam Long workflowId) throws IOException{
		return ResponseEntity.ok(workflowNodeService.findAllWorkflowNodes(companyId, workflowId));
	}
	

}
