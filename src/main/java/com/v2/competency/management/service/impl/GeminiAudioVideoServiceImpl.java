package com.v2.competency.management.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.v2.competency.management.service.GeminiAudioVideoService;
@Service
public class GeminiAudioVideoServiceImpl implements GeminiAudioVideoService{
	@Autowired
	PropertyConfig config;

	@Override
	public String processFile(String prompt, MultipartFile file) throws RuntimeException {
        String fileName = file.getOriginalFilename();
        
        String lowerCaseFileName = fileName.toLowerCase();
        
        // Check if fileName is not null and determine file type by extension
        try {
			if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
			    return generateQuestionsFromPdf(prompt, file);
			} else if (fileName != null && (fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav"))) {
			    return summarizeAudioFromMultipart_1(prompt, file);
			}
//			else if(fileName != null && fileName.toLowerCase().endsWith(".mp4")) {
//				return videoInput(prompt, file);
//			}
			else if (lowerCaseFileName.endsWith(".doc")) {
			   // return processDocFile(prompt, file);
				throw new RuntimeException("Unsupported file type. Only PDF, MP3 and MP4 are supported."); 
			} else if (lowerCaseFileName.endsWith(".txt")) {
				throw new RuntimeException("Unsupported file type. Only PDF, MP3 and MP4 are supported.");
			}
			else {
				throw new RuntimeException("Unsupported file type. Only PDF, MP3 and MP4 are supported.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage(), e);
		}
    }

    private String generateQuestionsFromPdf(String prompt, MultipartFile pdfFile) throws IOException {
    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName();

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            // Convert MultipartFile to byte array
            byte[] data = pdfFile.getBytes();

            // Create the model and send the request
          
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(
                ContentMaker.fromMultiModalData(prompt, PartMaker.fromMimeTypeAndData("application/pdf", data))
            );

            // Extract and return the output
            String output = response.getCandidates(0).getContent().getParts(0).getText();
            return output;
        }
    }

    private String summarizeAudioFromMultipart(String prompt, MultipartFile audioFile) throws IOException {
//        String projectId = "extreme-world-434410-u8";
//        String location = "asia-south1";
//        String modelName = "gemini-1.5-flash";
    	
    	String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName();

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            // Convert MultipartFile to byte array
            byte[] data = audioFile.getBytes();
            System.out.println("model name is "+modelName);
            // Create the model and send the request for audio summarization
            
            Part audio1 =  PartMaker.fromMimeTypeAndData(
                    "audio/mp3", data);
            
            
           Content content =   ContentMaker.fromMultiModalData(audio1, prompt);
            
            
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            GenerateContentResponse response = model.generateContent(
                ContentMaker.fromMultiModalData(
                    content
                ));

            // Extract and return the output
            String output = response.getCandidates(0).getContent().getParts(0).getText();
            return output;
        }
    }
    
    private String summarizeAudioFromMultipart_1(String prompt, MultipartFile audioFile) throws IOException {
//      String projectId = "extreme-world-434410-u8";
//      String location = "asia-south1";
//      String modelName = "gemini-1.5-flash";
  	
  	String projectId = config.getGeminiProjectId();
      String location = config.getGeminiLocation();
      String modelName = config.getGeminiModelName();

      try (VertexAI vertexAi = new VertexAI(projectId, location); ) {
          GenerationConfig generationConfig =
              GenerationConfig.newBuilder()
              	  .setResponseMimeType("application/json") 
                  .setMaxOutputTokens(8192)
                  .setTemperature(1F)
                  .setTopP(0.95F)
                  .build();
          List<SafetySetting> safetySettings = Arrays.asList(
            SafetySetting.newBuilder()
                .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .build(),
            SafetySetting.newBuilder()
                .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .build(),
            SafetySetting.newBuilder()
                .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .build(),
            SafetySetting.newBuilder()
                .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                .build()
          );
          
          GenerativeModel model =
             GenerativeModel.newBuilder()
                .setModelName(modelName)
                .setVertexAi(vertexAi)
                .setGenerationConfig(generationConfig)
                .setSafetySettings(safetySettings)
                .build();

       
          byte[] audio1Bytes = audioFile.getBytes();
          System.out.println("length of audio1Bytes "+audio1Bytes.length);

          Part audio1 = PartMaker.fromMimeTypeAndData(
            "audio/mpeg", audio1Bytes);

          Content content = ContentMaker.fromMultiModalData(audio1, prompt);
       //   ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);
          GenerateContentResponse response = model.generateContent(content);

          String output = response.getCandidates(0).getContent().getParts(0).getText();
          return output;
      } 
  }
    
    
    public String videoInput(String prompt, String videoPath) throws IOException {
        System.out.println("Entered the video processing gemini method...");
        
        String projectId = config.getGeminiProjectId();
        String location = config.getGeminiLocation();
        String modelName = config.getGeminiModelName();

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
            System.out.println("Vertex AI initialized successfully.");

            // Read video file
            File videoFile = new File(videoPath);
            System.out.println("Attempting to read video from path: " + videoPath);

            if (!videoFile.exists()) {
                System.out.println("ERROR: Video file does not exist at path: " + videoPath);
                return "Error: Video file not found.";
            }

            byte[] data = Files.readAllBytes(videoFile.toPath());
            System.out.println("Video file read successfully. Size (bytes): " + data.length);
            GenerationConfig generationConfig =
	                GenerationConfig.newBuilder()
	                    //.setResponseMimeType("application/json") // This is the key line
	                    .build();

            // Initialize model
            GenerativeModel model = new GenerativeModel(modelName, generationConfig, vertexAI);
            System.out.println("Generative model initialized with model name: " + modelName);

            // Send request
            System.out.println("Sending request to Gemini model...");
            GenerateContentResponse response = model.generateContent(
                ContentMaker.fromMultiModalData(prompt, PartMaker.fromMimeTypeAndData("video/mp4", data)) //video/webm
            );

            System.out.println("Response received from Gemini model.");

            String output = response.getCandidates(0).getContent().getParts(0).getText();
            System.out.println("Extracted response text: " + output);

            return output;

        } catch (Exception e) {
            System.out.println("ERROR in videoInput method: " + e.getMessage());
            e.printStackTrace(); // Full stack trace in logs
            return "Error during video analysis: " + e.getMessage();
        }
    }
    
    @Override
    public String videoInputWithGoogleCloudBucketUrl(String location, String modelName, String prompt, String googleCloudBucketUrl) throws IOException {
    	String projectId = config.getGeminiProjectId();
        //String location = config.getGeminiLocation();
        //String modelName = config.getGeminiModelName();
    		if(location == null) {
    			location = config.getGeminiLocation();
    		}
    		if(modelName ==null) {
    			modelName = config.getGeminiModelName();
    		}
    	System.out.println("in videoInputWithGoogleCloudBucketUrl ..generating anaalysis with "+modelName+" and "+location);

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {
        	GenerationConfig generationConfig =
	                GenerationConfig.newBuilder()
	                    .setResponseMimeType("application/json") // This is the key line
	                    .build();

            // Initialize model
            GenerativeModel model = new GenerativeModel(modelName, generationConfig, vertexAI);
            //System.out.println("Generative model initialized with model name: " + modelName);

            // Send request
            System.out.println("Sending request to Gemini model...");

            // STEP 2: The GCS URI string is passed directly to PartMaker.
            // The Vertex AI SDK handles fetching the file from the URI.
            GenerateContentResponse response = model.generateContent(
                ContentMaker.fromMultiModalData(
                    prompt,
                    PartMaker.fromMimeTypeAndData("video/mp4", googleCloudBucketUrl)
                )
            );

            System.out.println("Response received from Gemini model.");
            String output = response.getCandidates(0).getContent().getParts(0).getText();
           // System.out.println("Extracted response text: " + output);
            return output;

        } catch (Exception e) {
            System.out.println("ERROR in videoInputWithGcs method: " + e.getMessage());
            e.printStackTrace(); // Full stack trace in logs
            return "Error during video analysis: " + e.getMessage();
        }
    }
    
    @Override
    public String transcriptInput(String location, String modelName, String prompt, String transcript)
            throws IOException {

        String projectId = config.getGeminiProjectId();

        if (location == null) {
            location = config.getGeminiLocation();
        }

        if (modelName == null) {
            modelName = config.getGeminiModelName();
        }

        System.out.println("in transcriptInput ..generating analysis with "
                + modelName + " and " + location);

        try (VertexAI vertexAI = new VertexAI(projectId, location)) {

            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setResponseMimeType("application/json")
                            .build();

            // Initialize model
            GenerativeModel model =
                    new GenerativeModel(modelName, generationConfig, vertexAI);

            System.out.println("Sending transcript request to Gemini model...");

            // Send prompt + transcript to Gemini
            GenerateContentResponse response = model.generateContent(
                    ContentMaker.fromMultiModalData(
                            prompt,
                            PartMaker.fromMimeTypeAndData(
                                    "text/plain",
                                    transcript
                            )
                    )
            );

            System.out.println("Response received from Gemini model.");

            String output =
                    response.getCandidates(0)
                            .getContent()
                            .getParts(0)
                            .getText();

            return output;

        } catch (Exception e) {

            System.out.println(
                    "ERROR in transcriptInput method: " + e.getMessage());

            e.printStackTrace();

            return "Error during transcript analysis: " + e.getMessage();
        }
    }
    

	@Override
	public String processAudioFile(String prompt, File file) throws IOException {
		String projectId = config.getGeminiProjectId();
	      String location = config.getGeminiLocation();
	      String modelName = config.getGeminiModelName();

	      try (VertexAI vertexAi = new VertexAI(projectId, location); ) {
	          GenerationConfig generationConfig =
	              GenerationConfig.newBuilder()
	                  .setMaxOutputTokens(8192)
	                  .setTemperature(1F)
	                  .setTopP(0.95F)
	                  .build();
	          List<SafetySetting> safetySettings = Arrays.asList(
	            SafetySetting.newBuilder()
	                .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
	                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
	                .build(),
	            SafetySetting.newBuilder()
	                .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
	                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
	                .build(),
	            SafetySetting.newBuilder()
	                .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
	                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
	                .build(),
	            SafetySetting.newBuilder()
	                .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
	                .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
	                .build()
	          );
	          
	          GenerativeModel model =
	             GenerativeModel.newBuilder()
	                .setModelName(modelName)
	                .setVertexAi(vertexAi)
	                .setGenerationConfig(generationConfig)
	                .setSafetySettings(safetySettings)
	                .build();

	       
	          byte[] audio1Bytes = convertFileToByteArray(file);

	          Part audio1 = PartMaker.fromMimeTypeAndData(
	            "audio/mpeg", audio1Bytes);

	          Content content = ContentMaker.fromMultiModalData(audio1, prompt);
	       //   ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);
	          GenerateContentResponse response = model.generateContent(content);

	          String res = response.getCandidates(0).getContent().getParts(0).getText();
	          res = res.replace("`", "");
	  	    res = res.replace('\u00A0',' ');
	  		  res = res.replace("'", " ");
	  		
	  		 
	  		  
	  		 res = res.trim();
	  		 if(res.startsWith("json")) {
	  			 res = res.substring("json".length(), res.length());
	  		 }
	          return res;
	      } 
	}
	
	public static byte[] convertFileToByteArray(File file)  {
        try {
			FileInputStream fis = new FileInputStream(file);
			byte[] byteArray = new byte[(int) file.length()];
			fis.read(byteArray);
			fis.close();
			return byteArray;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
    }
	
	@Override
	public String updateSystemPrompt(
	        String location,
	        String modelName,
	        String prompt,
	        String input) throws IOException {

	    String projectId = config.getGeminiProjectId();

	    if (location == null) {
	        location = config.getGeminiLocation();
	    }

	    if (modelName == null) {
	        modelName = config.getGeminiModelName();
	    }

	    System.out.println(
	            "in updateSystemPrompt ..generating prompt with "
	                    + modelName + " and " + location);

	    try (VertexAI vertexAI = new VertexAI(projectId, location)) {

	        GenerationConfig generationConfig =
	                GenerationConfig.newBuilder()
	                        .setResponseMimeType("text/plain")
	                        .build();

	        GenerativeModel model =
	                new GenerativeModel(
	                        modelName,
	                        generationConfig,
	                        vertexAI);

	        System.out.println(
	                "Sending system prompt update request to Gemini model...");

	        String completePrompt =
	                prompt
	                        + "\n\n"
	                        + input;

	        System.out.println(
	                "Complete prompt length: "
	                        + completePrompt.length());

	        GenerateContentResponse response =
	                model.generateContent(
	                        ContentMaker.fromString(completePrompt)
	                );

	        System.out.println(
	                "Response received from Gemini model.");

	        String output =
	                response.getCandidates(0)
	                        .getContent()
	                        .getParts(0)
	                        .getText();

	        return output;

	    } catch (Exception e) {

	        System.out.println(
	                "ERROR in updateSystemPrompt method: "
	                        + e.getMessage());

	        e.printStackTrace();

	        return "Error while updating system prompt: "
	                + e.getMessage();
	    }
	}

}
