package com.v2.competency.management.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.googlecloud.vertex.ai.dto.RelevancyScoreForAnswerDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.service.RelevancyCheckerService;

@Service
public class RelevancyCheckerServiceImpl implements RelevancyCheckerService{
	
	@Autowired
	PropertyConfig config; 
	
	ObjectMapper mapper = new ObjectMapper();
	
	
	
	 String json;
	 
	 @PostConstruct
	 public void init() throws JsonProcessingException {
		 json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new RelevancyScoreForAnswerDto());
		 System.out.println("in RelevancyCheckerServiceImpl "+json);
	 }

	@Override
	public RelevancyScoreForAnswerDto checkIfAnswerRelevant(VFTestUserQuestionAnswer answer) throws IOException {
		// TODO Auto-generated method stub
		
		String projectId = config.getGeminiProjectId();
	    String location = config.getGeminiLocation();
	    String modelName = config.getGeminiModelName();

	    
	    String questionText = answer.getQuestion();
	    String answerText = answer.getAnswer();
	    String parentCompetency = answer.getParentCompetency();
	    String competency = answer.getCompetency();

	   
	    String textPrompt = "You are a professional assessor. Please strictly evaluate the relevance of the following answer to the given question on a scale as a percentage." +System.lineSeparator() + System.lineSeparator() +
	                    "Question: " + questionText +System.lineSeparator() +
	                    "Answer: " + answerText +System.lineSeparator() +
	                    "Parent Competency: " + parentCompetency +System.lineSeparator() +
	                    "Competency: " + competency  +System.lineSeparator() +
	                    "Provide your response only as a json below"+System.lineSeparator()+
	                    ""+json;
	    
	    textPrompt += " Response should not include any label or header and should strictly be in json format. If answers are irrelevant, score them as 0";
	    
	    String res = textInput(projectId, location, modelName, textPrompt);
	    res = res.replace("`", "");
	    res = res.replace('\u00A0',' ');
		  res = res.replace("'", " ");
		  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		 
		  
		 res = res.trim();
		 if(res.startsWith("json")) {
			 res = res.substring("json".length(), res.length());
		 }
		 System.out.println("relevancy output from ai "+res);
	    RelevancyScoreForAnswerDto rel =  mapper.readValue(res.getBytes(), RelevancyScoreForAnswerDto.class);
	    
	    return rel;
	    
	}
	
	public static String textInput(String projectId, String location, String modelName, String textPrompt) throws IOException {
		
		try (VertexAI vertexAI = new VertexAI(projectId, location)) {
		      GenerativeModel model = new GenerativeModel(modelName, vertexAI);
		     
		      List<SafetySetting> safetyList = Arrays.asList( SafetySetting.newBuilder()
				      .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
				      .build());
		      GenerateContentResponse response = model.generateContent(textPrompt, safetyList);
		 //  model.generateContent(null, null);
		      String output = ResponseHandler.getText(response);
		      return output;
		    }
		
	}
		
	}


