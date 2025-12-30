package com.v2.competency.management.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
public class VideoAgentMetaDataController {
	
	@Autowired
	PropertyConfig config;
	
	String prompt;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping(value = "/fetchOpeningStatementsForVideoAgent", method = RequestMethod.GET)
	public ResponseEntity<?> fetchOpeningStatementsForVideoAgent( @RequestParam String token, @RequestParam String companyId, @RequestParam String agentType,  @RequestParam String industry, 
			@RequestParam String company, @RequestParam(required = false) String product) throws IOException{
        String promptTemplate = readClasspathFile("openingStatementPrompt.txt");
        String prompt = promptTemplate.replace("{{agent_type}}", agentType);
        prompt = prompt.replace("{{company_name}}", company);
        prompt = prompt.replace("{{product_name}}", product==null?"Not Provided":product);

        try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
   	      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
   	      GenerateContentResponse response = model.generateContent(
   	    		  prompt
   	      );
   	    String res =  response.getCandidates(0).getContent().getParts(0).getText();
   	    res = res.replace("`", "");
   	    res = res.replace('\u00A0',' ');
   		  res = res.replace("'", " ");
   		 res = res.trim();
   		 if(res.startsWith("json")) {
   			 res = res.substring("json".length(), res.length());
   		 }
   		 System.out.println("res "+res);
   		List<String> list = objectMapper.readValue(
   	            res, 
   	            new TypeReference<List<String>>(){} // <--- This captures the generic type
   	        );
   	   return ResponseEntity.ok(list);
        }
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
	
	@RequestMapping(value = "/fetchPromptTemplateVariantsForVideoAgentKnowledgebase", method = RequestMethod.GET)
	public ResponseEntity<?> fetchPromptTemplateVariantsForVideoAgentKnowledgebase( @RequestParam String token, @RequestParam String companyId, @RequestParam String agentType,  @RequestParam String industry, 
			@RequestParam String company, @RequestParam(required = false) String product) throws IOException{
        String promptTemplate = readClasspathFile("knowledbasePromptHeyGen.txt");
        String prompt = promptTemplate.replace("{{agent_type}}", agentType);
        prompt = prompt.replace("{{company_name}}", company);
        prompt = prompt.replace("{{product_name}}", product==null?"Not Provided":product);

        try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
   	      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
   	      GenerateContentResponse response = model.generateContent(
   	    		  prompt
   	      );
   	    String res =  response.getCandidates(0).getContent().getParts(0).getText();
   	    res = res.replace("`", "");
   	    res = res.replace('\u00A0',' ');
   		  res = res.replace("'", " ");
   		 res = res.trim();
   		 if(res.startsWith("json")) {
   			 res = res.substring("json".length(), res.length());
   		 }
   		 System.out.println("res "+res);
   		List<String> list = objectMapper.readValue(
   	            res, 
   	            new TypeReference<List<String>>(){} // <--- This captures the generic type
   	        );
   	   return ResponseEntity.ok(list);
        }
	}
	

}
