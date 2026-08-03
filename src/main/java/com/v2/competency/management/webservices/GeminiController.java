package com.v2.competency.management.webservices;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.googlecloud.heygen.endsuer.customer.dto.Root;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@RequestMapping("/gemini")
public class GeminiController {
	
	String defaultPrompt;
	
	@Autowired
	PropertyConfig config;
	
	ObjectMapper mapper = new ObjectMapper();
	
	private String generateAnalysisFromTranscript(String prompt, String transcript) throws IOException {
    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName();
        	if(prompt == null) {
        		prompt = defaultPrompt;
        	}

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
           // byte[] data = transcript.getBytes();

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(prompt, transcript) 
                );

            // Extract and return the output
            String output = response.getCandidates(0).getContent().getParts(0).getText();
            return output;
        }
    }
	
	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		try {
			defaultPrompt =  new String( Files.readAllBytes(
					Paths.get(GeminiController.class.getResource("/gemini_heygen_liveavatar_prompt.txt").toURI())) );
		} catch (Exception e) {
			try {
				defaultPrompt =  new String( Files.readAllBytes(
						Paths.get(GeminiController.class.getResource("gemini_heygen_liveavatar_prompt.txt").toURI())) );
			} catch (Exception e1) {
				defaultPrompt = FileUtils.readFileToString(new File("/opt/apps/ai_ass/gemini_heygen_liveavatar_prompt.txt"));
			} 
		} 
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	}
	
	
	@PostMapping("/analysis")
    public Root analysis(
                               @RequestBody String transcript, @RequestBody(required = false) String prompt, 
                               @RequestParam String token) throws IOException {
		
		String res = generateAnalysisFromTranscript(prompt, transcript);
		res = res.replace("`", "");
	    res = res.replace('\u00A0',' ');
		res = res.replace("'", " ");
		res = res.trim();
			 if(res.startsWith("json")) {
				 res = res.substring("json".length(), res.length());
			 }
		System.out.println(res);
		return mapper.readValue(res, Root.class);
	}
	

}
