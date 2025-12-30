package com.v2.competency.management.webservices;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.aiplatform.v1.Endpoint;
import com.google.cloud.aiplatform.v1.EndpointServiceClient;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.protobuf.ByteString;
import com.v2.competency.management.service.GeminiAudioVideoService;
@RestController
public class AudioGeminiWebService {
	@Autowired
	GeminiAudioVideoService geminiAudioVideoService;
	
	
	@CrossOrigin
	@RequestMapping(value = "/audioProcessing", method = RequestMethod.POST)
	 public ResponseEntity<?> audioProcessing( @RequestParam MultipartFile file,  
	           HttpSession session, @RequestParam String prompt, @RequestParam String token) throws Exception{  
		String res = geminiAudioVideoService.processFile(prompt, file);
		return ResponseEntity.ok(res);
	}
	
	
	
	public static void analyzeAudio(String modelEndpoint, String audioFilePath, String prompt) throws IOException {
        // Initialize Endpoint and Prediction clients
//        EndpointServiceClient endpointClient = EndpointServiceClient.create();
//        PredictionServiceClient predictionClient = PredictionServiceClient.create();
//
//        // Create Endpoint reference
//        Endpoint endpoint = Endpoint.newBuilder()
//                .setName(modelEndpoint)
//                .build();
//
//        // Create PredictRequest
//        PredictRequest predictRequest = PredictRequest.newBuilder()
//                .setEndpoint(endpoint.getName())
//                .putAllInstances(Map.of("audio", ByteString.readFrom(new FileInputStream(audioFilePath))))
//                .setParameters(Map.of("prompt", prompt))
//                .build();
//
//        // Send the request and get the response
//        PredictResponse predictResponse = predictionClient.predict(predictRequest);
//
//        // Process the response
//        System.out.println(predictResponse.getPredictionsList());
    }
	
	//String modelEndpoint = "projects/my-project-id/locations/us-central1/endpoints/my-gemini-model";


}
