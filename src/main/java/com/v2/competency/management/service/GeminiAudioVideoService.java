package com.v2.competency.management.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface GeminiAudioVideoService {
	
	
	public String processFile(String prompt, MultipartFile file);
	
	public String processAudioFile(String prompt, File file) throws IOException;
	
	public String videoInput(String prompt, String videoPath) throws IOException;
	
	public String videoInputWithGoogleCloudBucketUrl(String location, String model, String prompt, String googleCloudBucketUrl) throws IOException ;
	
	public String transcriptInput(String location, String modelName, String prompt, String transcript)
            throws IOException;
	
	public String updateSystemPrompt(
	        String location,
	        String modelName,
	        String prompt,
	        String input) throws IOException;

}
