package com.v2.competency.management.webservices;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.GeminiAudioVideoService;

@RestController
public class TestingAudioServiceForScenarioQuestions {
	
	@Autowired
	AIResponseGeneratorService service; 
	
	@Autowired
	GeminiAudioVideoService service2;
	
	@PostMapping("/testAudioVideo")
	public ResponseEntity<String> testVoiceWithOurPrompt(String question, MultipartFile audioOrVideoLink, String token) throws IOException{
		
		 try {
	            // Call the service method
	            String response = service.generateAnalysisForScenarioBasedQuestionUsingAudioVideoLink(question, audioOrVideoLink);
	            
	            // Return the response as a JSON object
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            // Handle any other exceptions
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("An unexpected error occurred: " + e.getMessage());
	        }
		
		
		
	}
	
	@PostMapping("/testAudioVideo2")
	public String testVoiceWithOgCode(String prompt, MultipartFile audioOrVideoLink, String token){
		
		String res = service2.processFile(prompt, audioOrVideoLink);
		
		return res;
		
	}
	
	
}



