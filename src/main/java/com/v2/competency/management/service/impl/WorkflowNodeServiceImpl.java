package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.WorkflowNodeType;
import com.v2.competency.management.entities.WorkflowNode;
import com.v2.competency.management.repos.WorkflowNodeRepo;
import com.v2.competency.management.service.WorkflowNodeService;

@Service
@Transactional
public class WorkflowNodeServiceImpl implements WorkflowNodeService{
	@Autowired
	WorkflowNodeRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public WorkflowNode findUniqueWorkflowNode(Integer position, Long workFlowId, String companyId) {
		return repo.findUniqueWorkflowNode(position, workFlowId, companyId);
	}
	
	private void validate(WorkflowNode workflowNode) {
		if(workflowNode.getPosition() == null) {
			throw new RuntimeException("Position can not be null");
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())) {
			if(workflowNode.getVideoAgentTraining() == null) {
				throw new RuntimeException("Video Agent (Training) can not be null");
			}
			else {
				if(workflowNode.getRolePlayAssessment() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayTraining() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults() != null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations() != null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType());
				}
			}
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType()) ){
			if(workflowNode.getRolePlayTraining() == null) {
				throw new RuntimeException("Role Play (Training) can not be null");
			}
			else {
				if(workflowNode.getRolePlayAssessment() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTraining() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults() != null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations() != null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.ROLEPLAY_TRAINING.getWorkflowNodeType());
				}
			}
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())) {
			if(workflowNode.getRolePlayAssessment() == null) {
				throw new RuntimeException("Role Play (Assessment) can not be null");
			}
			else {
				if(workflowNode.getRolePlayTraining() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTraining() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults() != null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations() != null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType());
				}
			}
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType()) ){
			if(workflowNode.getShowResults() == null || !workflowNode.getShowResults()) {
				throw new RuntimeException("Show Results flag can not be null or false");
			}
			else {
				if(workflowNode.getRolePlayTraining() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTraining() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayAssessment() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowTrainingRecommendations() != null && workflowNode.getShowTrainingRecommendations()) {
					throw new RuntimeException("Can not have  'Show Training Recommendations' flag enabled if type is "+WorkflowNodeType.SHOW_RESULTS.getWorkflowNodeType());
				}
			}
		}
		
		if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType())) {
			if(workflowNode.getShowTrainingRecommendations() == null || !workflowNode.getShowTrainingRecommendations()) {
				throw new RuntimeException("Show Training Recommendations flag can not be null or false");
			}
			else {
				if(workflowNode.getRolePlayTraining() != null) {
					throw new RuntimeException("Can not have  Role Play Training if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getVideoAgentTraining() != null) {
					throw new RuntimeException("Can not have  Video Agent Training if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getRolePlayAssessment() != null) {
					throw new RuntimeException("Can not have  Role Play Assessment if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
				
				if(workflowNode.getShowResults() != null && workflowNode.getShowResults()) {
					throw new RuntimeException("Can not have  'Show Results' flag enabled if type is "+WorkflowNodeType.SHOW_TRAINING_RECOMMENDATIONS.getWorkflowNodeType());
				}
			}
		}
		
		validatePositionValue(workflowNode);
	}
	
	private void validatePositionValue(WorkflowNode workflowNode) {
		List<WorkflowNode> list =   repo.findAllWorkflowNodes(workflowNode.getCompanyId(), workflowNode.getWorkFlowId());
		if(  !(workflowNode.getPosition() > 0 && workflowNode.getPosition() <= list.size() +1)) {
			throw new RuntimeException("For an update node request position should be between 1 and "+list.size()+". For a create node request posiiton should be "+list.size() +1);
		}
	}
	
//	private void canBeUpdated(WorkflowNode workflowNode, Long workFlowId, String companyId) {
//		//get the prior node at the same position
//		WorkflowNode prior = findUniqueWorkflowNode(workflowNode.getPosition(), workFlowId, companyId);
//		
//		if(prior.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())  && !(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType()))) {
//			//assessment node is getting replaced by non-assessment node
//			//check if it can be allowed.
//			boolean canBeUpdated = false;
//			List<WorkflowNode> list = findAllWorkflowNodes(companyId, workFlowId);
//			
//				/**
//				 * there is another roleplay in a different position that 
//				 */
//				for(WorkflowNode node :list) {
//					if(workflowNode.getPosition() != node.getPosition() ) {
//						if(workflowNode.getType().equalsIgnoreCase(WorkflowNodeType.ROLPLAY_ASSESSMENT.getWorkflowNodeType())) {
//							canBeUpdated = true;
//						}
//					}
//				}
//		}
//		
//	}

	@Override
	public WorkflowNode saveOrUpdate(WorkflowNode workflowNode) {
		validate(workflowNode);
		WorkflowNode workflowNode2 = findUniqueWorkflowNode(workflowNode.getPosition(), workflowNode.getWorkFlowId(), workflowNode.getCompanyId());
		
		if(workflowNode2 == null) {
			workflowNode.setCreateDate(new Date());
			return repo.save(workflowNode);
		}
		
		workflowNode.setId(workflowNode2.getId());
		workflowNode.setCreateDate(workflowNode2.getCreateDate());
		workflowNode.setUpdateDate(new Date());
		mapper.map(workflowNode, workflowNode2);
		
		return repo.save(workflowNode2);
	}

	@Override
	public List<WorkflowNode> findAllWorkflowNodes(String companyId, Long workFlowId) {
		return repo.findAllWorkflowNodes(companyId, workFlowId);
	}

}
