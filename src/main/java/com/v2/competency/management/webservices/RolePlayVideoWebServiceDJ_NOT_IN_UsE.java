package com.v2.competency.management.webservices;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.v2.competency.management.entities.RolePlayQuestionAnswer;
import com.v2.competency.management.entities.RolePlayQuestionFollowUpLevel;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.repos.RolePlayQuestionAnswerRepo;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.RelevancyCheckerService;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;

@RestController
public class RolePlayVideoWebServiceDJ_NOT_IN_UsE {
	
	@Autowired
	GeminiAudioVideoService geminiservice;
	
	
	
	@Autowired
	RolePlayQuestionAnswerRepo repo;
	
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
	
//	@PostMapping("/videoTest")
//	public String videoAnalysis(String prompt, String path, String token) throws IOException {
//		
//		String res = geminiservice.videoInput(prompt, path);
//		
//		return res;
//		
//	}
	
	String projectId = "contactaiassessments";
    String location = "asia-south1";
   // String modelName = "gemini-1.0-pro-vision";//mine old
    String modelName = "gemini-1.5-flash-001"; //dhruv
    
    String defPrompt = " You are an active participant in a roleplay assessment, responding directly as the character in the scenario. Below is the transcript of the conversation so far:\r\n"
    		+ "\r\n"
    		+ "${TRANSCRIPT}\r\n"
    		+ "\r\n"
    		+ "Based on the employee's responses, generate a follow-up question that is engaging, tricky, and relevant to the conversation. Ensure that your question maintains a natural flow, challenges the employee's reasoning or explanation, and encourages deeper thinking. Strictly respond in the first person, as if you are the actual customer, client, or role-specific character engaging with the employee. Do NOT provide any context, explanations, or third-party narration—only return the follow-up question in a natural conversational tone.\r\n"
    		+ "\r\n"
    		+ "If the employee responds with uncertainty, such as 'I don't know' or 'No clue,' respond in character by questioning their lack of knowledge, such as 'How come you don’t know this when you are working here?' or 'I was expecting someone in your position to be aware of this.' After that, repeat the original question in simpler terms to give them another chance to answer. If the user answers something irrelevantm, then the next question should be I did not get your point, let me repeat the question again for you and then repeat the question. If the response is too short, acknowledge it by saying, 'That was a very brief answer. Could you elaborate on that?' and then rephrase the follow-up question to encourage a more detailed response. Ensure that follow-up questions build upon previous responses logically, making the conversation feel natural, dynamic, and immersive. Keep your tone and wording consistent with the role you are playing, whether it's a demanding customer, a curious client, or a strict manager. You are strictly allowed to talk in first person only and must stay within the role without breaking character at any point. ";
	
//	@PostMapping("/nextRolePlayQuestionWithVideo")
	
	
	

    private String geminiService(String transcript, String defaultPrompt) throws IOException {
		
//    	String projectId = "extreme-world-434410-u8";
//	    String location = "asia-south1";
//	    String modelName = "gemini-1.5-flash";
    	String textPrompt = null;
    		if(defaultPrompt == null) {
    			textPrompt = defPrompt.replace("${TRANSCRIPT}", transcript);
    		}
    		else {
    			textPrompt = defaultPrompt.replace("${TRANSCRIPT}", transcript);
    		}

	  
	    
	   // String textPrompt = "Keeping "+transcript+" in mind, generate a follow up question that is very relevant to the answer of the latest question given and it makes this roleplay very realistic. Return a question which is the most relevant and looks realistic to the roleplay, keeping the whole transcript in mind.";

	    String output = textInput(projectId, location, modelName, textPrompt);
	    
	    return output;
    	
	}
    
	public static String textInput(String projectId, String location, String modelName, String textPrompt) throws IOException {
			
			try (VertexAI vertexAI = new VertexAI(projectId, location)) {
			      GenerativeModel model = new GenerativeModel(modelName, vertexAI);
	
			      GenerateContentResponse response = model.generateContent(textPrompt);
			      String output = ResponseHandler.getText(response);
			      return output;
			    }
			
		}

	private RolePlayQuestionFollowUpLevel getNextLevel(RolePlayQuestionFollowUpLevel currentLevel) {
        switch (currentLevel) {
            case START:
                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_1;
            case FOLLOW_UP_1:
                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_2;
            case FOLLOW_UP_2:
                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_3;
            default:
                return RolePlayQuestionFollowUpLevel.FOLLOW_UP_3;
        }
    }


}
