package com.v2.competency.management.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.googlecloud.vertex.ai.workflow.insights.dto.Overall;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.WorkflowNode;
import com.v2.competency.management.entities.WorkflowNodeSession;
import com.v2.competency.management.entities.WorkflowRecommGenerator;
import com.v2.competency.management.entities.WorkflowSession;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.repos.WorkflowNodeSessionRepo;
import com.v2.competency.management.repos.WorkflowSessionRepo;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.WorkflowNodeService;
import com.v2.competency.management.service.WorkflowNodeSessionService;
import com.v2.competency.management.service.WorkflowSessionService;
import com.v2.competency.management.service.ZimulateWorkflowService;
@Service
public class WorkflowRecommGeneratorImpl implements WorkflowRecommGenerator{
	
	@Autowired
	ZimulateWorkflowService zimulateWorkflowService;
	
	@Autowired
	WorkflowSessionService workflowSessionService;
	
	@Autowired
	WorkflowNodeService workflowNodeService;
	
	@Autowired
	WorkflowSessionRepo workflowSessionRepo;
	
	@Autowired
	VFRolePlayTestSessionRepo rolePlayTestSessionRepo;
	
	@Autowired
	VFRolePlayTestService vfRolePlayTestService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	WorkflowNodeSessionService workflowNodeSessionService;
	
	@Autowired
	WorkflowNodeSessionRepo workflowNodeSessionRepo;

	@Override
	public boolean checkIfRecommCanBeGenerated(Long rolePlayAssessmentId, Long workflowSessionId) {
		if(rolePlayAssessmentId == null || workflowSessionId == null) {
			return false;
		}
		WorkflowSession workflowSession = workflowSessionRepo.findById(workflowSessionId).get();
		ZimulateWorkflow workflow =  workflowSession.getWorkflow();
		VFRolePlayTestSession rolePlayTestSession =   rolePlayTestSessionRepo.findById(rolePlayAssessmentId).get();
		VFRolePlayTest rolePlayTest = vfRolePlayTestService.findUniqueRecord(rolePlayTestSession.getTestIdentifier(), rolePlayTestSession.getCompanyId());
		List<WorkflowNode> nodes =  workflowNodeService.findAllWorkflowNodes(workflow.getCompanyId(), workflow.getId());
		List<WorkflowNode> nodesSub = new ArrayList<>();
		Integer count = 0;
			for(WorkflowNode workflowNode : nodes) {
				count++;
				if(workflowNode.getRolePlayAssessment() != null && workflowNode.getRolePlayAssessment().getId() == rolePlayTest.getId()) {
					nodesSub = nodes.subList(count, nodes.size());
					break;
				}
			}
		if(nodesSub.size() == 0) {
			return false;
		}
		
		for(WorkflowNode workflowNode : nodesSub) {
			/**
			 * If any next node exists of type assessment, do not generate recomm.
			 */
			if(workflowNode.getRolePlayAssessment() != null) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	@Async
	public String generateRecommendations(Long workflowSessionId) throws IOException  {
		List<VFRolePlayTestSession> sessions = rolePlayTestSessionRepo.findRoleplaySessionsByWorkflowSessionId(workflowSessionId);
		String prompt = readClasspathFile("gapsAnalyzingPrompt.txt");
		String scenarios = "";
		Integer count = 1;
		boolean generate = false;
			for(VFRolePlayTestSession session : sessions) {
				scenarios += count+".1 Role play Scenario - "+System.lineSeparator();
				VFRolePlayTest rolePlayTest = vfRolePlayTestService.findUniqueRecord(session.getTestIdentifier(), session.getCompanyId());
				scenarios += rolePlayTest.getQuestionText()+System.lineSeparator();
				String insights = session.getVideoInsightsJson();
					if(insights != null) {
						scenarios +=  count+".2 Assessment Score";
						scenarios += System.lineSeparator() + insights + System.lineSeparator();
						generate = true;
					}
					scenarios += "-----------------------------------------------"+System.lineSeparator();	
			}

			if(!generate) {
				return null;
			}
	
		prompt = prompt.replace("${SCENARIOS}", scenarios);
		String res =  null;
		try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
	   	      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
	   	      GenerateContentResponse response = model.generateContent(
	   	    		  prompt
	   	      );
	   	    res =  response.getCandidates(0).getContent().getParts(0).getText();
	   	    res = res.replace("`", "");
	   	    res = res.replace('\u00A0',' ');
	   		  res = res.replace("'", " ");
	   		 res = res.trim();
	   		 if(res.startsWith("json")) {
	   			 res = res.substring("json".length(), res.length());
	   		 }
	   		 System.out.println("res "+res);
	   		WorkflowNodeSession recomm =  workflowNodeSessionService.findShowRecommNodeForWorkflowSession(workflowSessionId);
	   		recomm.setRecommendations(res);
	   		workflowNodeSessionRepo.save(recomm);
	        }
        
		//Overall
        return res;
	}
	
	public String readClasspathFile(String fileName) throws IOException {
	    // 1. Get the InputStream from the classpath
	    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
	        
	        if (inputStream == null) {
	            throw new IOException("File not found: " + fileName);
	        }

	        // 2. Use IOUtils to read the stream into a String
	        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
	    }
	}

}
