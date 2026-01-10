package com.v2.competency.management.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v2.competency.management.dtos.WorkflowNodeType;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VideoAgentSession;
import com.v2.competency.management.entities.WorkflowNodeSession;
import com.v2.competency.management.entities.WorkflowSession;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.repos.VideoAgentSessionRepo;
import com.v2.competency.management.repos.WorkflowNodeSessionRepo;
import com.v2.competency.management.service.WorkflowNodeSessionService;
import com.v2.competency.management.service.WorkflowSessionService;
@Service
@Transactional
public class WorkflowNodeSessionServiceImpl implements WorkflowNodeSessionService {
	@Autowired
	WorkflowNodeSessionRepo repo;
	
	@Autowired
	VideoAgentSessionRepo videoAgentSessionRepo;
	
	@Autowired
	WorkflowSessionService workflowSessionService;
	
	@Autowired
	VFRolePlayTestSessionRepo rolePlayTestSessionRepo;
	
	@Autowired
	WorkflowNodeSessionService workflowNodeSessionService;

	@Override
	public WorkflowNodeSession findUniqueWorkflowNodeSession(Long workFlowId, Long workFlowSessionId, Long workFlowNodeInstanceId, String email, Integer position,
			String companyId) {
		return repo.findUniqueWorkflowNodeSession(workFlowId, workFlowSessionId, workFlowNodeInstanceId, email, position, companyId);
	}

	@Override
	public List<WorkflowNodeSession> findAllWorkflowNodesByCompanyId(String companyId, Long workFlowId, Long workFlowSessionId,  String email) {
		return repo.findAllWorkflowNodesByCompanyId(companyId, workFlowId, workFlowSessionId, email);
	}

	@Override
	public WorkflowNodeSession markComplete(WorkflowNodeSession node) {
		WorkflowSession workflowSession = workflowSessionService.findUniqueWorkflowSession(node.getWorkflowId(), node.getEmail(), node.getCompanyId());
		WorkflowNodeSession workflowNodeSession =  workflowNodeSessionService.findUniqueWorkflowNodeSession(node.getWorkflowId(), node.getWorkflowSessionId(), node.getWorkflowNodeInstanceId(), 
				node.getEmail(), node.getPosition(), node.getCompanyId());
		if(workflowNodeSession != null) {
			return workflowNodeSession;//already instance created.
		}
		
		if(workflowSession == null) {
			throw new RuntimeException("Workflow Session does not exist for workflow id passed "+node.getWorkflowId());
		}
		
		
		
		if(node.getNodeType().equalsIgnoreCase(WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())) {
			VideoAgentSession videoAgentSession = videoAgentSessionRepo.findById(node.getWorkflowNodeInstanceId()).get();
			if(videoAgentSession == null) {
				throw new RuntimeException("Video Agent Session does not exist for id passed "+node.getWorkflowNodeInstanceId());
			}
			node.setVideoAgentSession(videoAgentSession);
			node.setRolePlayTrainingSession(null);
			node.setRolePlayAssessmentSession(null);
			node.setResultsShown(false);
			node.setRecommendationsShown(false);
			return repo.save(node);
		}
		else if(node.getNodeType().equalsIgnoreCase(WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType())) {
			VFRolePlayTestSession rolePlayTrainingSession = rolePlayTestSessionRepo.findById(node.getWorkflowNodeInstanceId()).get();
			if(rolePlayTrainingSession == null) {
				throw new RuntimeException("Roleplay Training Session does not exist for id passed "+node.getWorkflowNodeInstanceId());
			}
			node.setRolePlayTrainingSession(rolePlayTrainingSession);
			node.setVideoAgentSession(null);
			node.setRolePlayAssessmentSession(null);
			node.setResultsShown(false);
			node.setRecommendationsShown(false);
			return repo.save(node);
		}
		else if(node.getNodeType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())) {
			VFRolePlayTestSession rolePlayAssessmentSession = rolePlayTestSessionRepo.findById(node.getWorkflowNodeInstanceId()).get();
			if(rolePlayAssessmentSession == null) {
				throw new RuntimeException("Roleplay Assessment Session does not exist for id passed "+node.getWorkflowNodeInstanceId());
			}
			node.setVideoAgentSession(null);
			node.setRolePlayTrainingSession(null);
			node.setRolePlayAssessmentSession(rolePlayAssessmentSession);
			node.setResultsShown(false);
			node.setRecommendationsShown(false);
			return repo.save(node);
		}
		else if(node.getNodeType().equalsIgnoreCase(WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType())) {
			node.setResultsShown(true);
			node.setVideoAgentSession(null);
			node.setRolePlayTrainingSession(null);
			node.setRolePlayAssessmentSession(null);
			node.setRecommendationsShown(false);
			return repo.save(node);
		}
		else if(node.getNodeType().equalsIgnoreCase(WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType())) {
			node.setResultsShown(false);
			node.setVideoAgentSession(null);
			node.setRolePlayTrainingSession(null);
			node.setRolePlayAssessmentSession(null);
			node.setRecommendationsShown(true);
			return repo.save(node);
		}
		
		throw new RuntimeException("Invalid status of node type "+node.getNodeType());
	}

	@Override
	public WorkflowNodeSession findById(Long id) {
		return repo.findById(id).get();
	}

	@Override
	public WorkflowNodeSession findShowRecommNodeForWorkflowSession(Long workflowSessionId) {
		return repo.findShowRecommNodeForWorkflowSession(workflowSessionId);
	}

}
