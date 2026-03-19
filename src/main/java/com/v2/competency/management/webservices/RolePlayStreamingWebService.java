package com.v2.competency.management.webservices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.repos.RolePlayQuestionAnswerRepo;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.RelevancyCheckerService;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.service.VideoMergerServiceDJ;
import com.v2.competency.management.service.impl.PropertyConfig;
import com.v2.competency.management.service.impl.QueueManager;

@RestController
public class RolePlayStreamingWebService {
	
	
	@Autowired
	RolePlayQuestionAnswerRepo repo; 
	
	@Autowired
	GeminiAudioVideoService geminiservice;
	
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
	
	@Autowired
	VideoMergerServiceDJ videoService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	QueueManager queueManager; 
	
	
	
	String unifiedRolePlayInsightsPrompt = 
		    "You are provided with a role play interview for a Customer Service Executive, which includes both a **video recording** and its **transcript**." + System.lineSeparator() +
		    System.lineSeparator() +
		    "**Transcript**:" + System.lineSeparator() +
		    "${TRANSCRIPT}" + System.lineSeparator() +
		    System.lineSeparator() +
		    "Analyze the candidate using **both the transcript and the video** on the following parameters:" + System.lineSeparator() +
		    "${PARAMETERS}" + System.lineSeparator() +
		    System.lineSeparator() +
		    "Here is additional guidance for evaluating the video:" + System.lineSeparator() +
		    "• **Confidence** – Rate the speaker's confidence level on a scale of 1–10." + System.lineSeparator() +
		    "• **Fluency** – Assess speech flow and articulation." + System.lineSeparator() +
		    "• **Accent** – Identify any prominent accents and evaluate clarity." + System.lineSeparator() +
		    "• **Way of Speaking** – Describe the speaker’s tone, enthusiasm, and clarity." + System.lineSeparator() +
		    "• **Body Language** – Evaluate gestures, posture, and facial expressions." + System.lineSeparator() +
		    "• **Grammar** – Identify any grammatical errors or mispronunciations." + System.lineSeparator() +
		    System.lineSeparator() +
		    "Provide the output strictly in the following **structured JSON** format:" + System.lineSeparator() +
		    "${ANALYSIS_JSON}" + System.lineSeparator() +
		    System.lineSeparator() +
		    "If the video does not contain any relevant speech (e.g., no speaker present, background noise only, or silent footage), return the JSON structure with a score of 0 for all parameters.";
	
	
	@Value("${video.storage.directory:/opt/eAssess/apache-tomcat-9.0.93/webapps/ROOT/audio}")
	private String baseLoc;

	private final Map<String, Path> activeStreams = new ConcurrentHashMap<>();

	@PostMapping("/receive-video")
	public ResponseEntity<String> receiveVideo(HttpServletRequest request,
	        @RequestParam String email,
	        @RequestParam String firstName,
	        @RequestParam String lastName,
	        @RequestParam String testName,
	        @RequestParam Integer attempt,
	        @RequestParam String companyId,
	        @RequestParam(value = "sessionId", required = false) String sessionId,
	        @RequestParam(value = "isFinal", required = false) boolean isFinal) {

	    boolean isNewSession = false;

	    if (sessionId == null || sessionId.isEmpty()) {
	        sessionId = UUID.randomUUID().toString(); // Generate a new session ID
	        isNewSession = true;
	    }

	    InputStream inputStream = null;
	    try {
	        inputStream = request.getInputStream();

	        // Construct the directory structure
	        String loc = baseLoc + File.separator + companyId + File.separator + testName + File.separator + email + File.separator + attempt;
	        File folder = new File(loc);
	        if (!folder.exists()) {
	            folder.mkdirs();
	        }

	        // Create or reuse the output file path
	        Path outputFile = activeStreams.get(sessionId);
	        if (outputFile == null) {
	            String filename = "merged_" + System.currentTimeMillis() + ".mp4";
	            File destinationFile = new File(folder, filename);
	            outputFile = destinationFile.toPath();
	            activeStreams.put(sessionId, outputFile);
	            isNewSession = true;
	        }

	        try (OutputStream outputStream = Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
	            long bytesCopied = inputStream.transferTo(outputStream);

	            if (isFinal) {
	                // Cleanup session
	                activeStreams.remove(sessionId);

	                // Generate merged video path
	                String mergedVideoPath = outputFile.toString(); 

	                // Build session
	                VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	                session.setTestIdentifier(testName);
	                session.setVideoLink(mergedVideoPath);

	                // Set video URL
	                String mergedFileName = Paths.get(mergedVideoPath).getFileName().toString();
	                String fileUrl = config.getFileServerBaseUrl() + companyId + "/" + testName + "/" + email + "/" + attempt + "/" + mergedFileName;
	                session.setVideoUrl(fileUrl);

	                // Save session
	                session = rolePlayTestSessionService.saveOrUpdate(session);

	                // Trigger async insights generation
	                aSyncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithUnifiedPromptInAsyncDJ(testName, email, companyId, session, mergedVideoPath);

	                return ResponseEntity.ok("OK"); 
	            } else {
	                if (isNewSession) {
	                    return ResponseEntity.ok(sessionId);
	                } else {
	                    return ResponseEntity.ok(sessionId);
	                }
	            }
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	        activeStreams.remove(sessionId);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving video: " + e.getMessage());
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close();
	            } catch (IOException e) {
	                System.err.println("Error closing input stream: " + e.getMessage());  
	            }
	        }
	    }
	}
	
	
	@PostMapping("/submit-video")
	public ResponseEntity<String> submitFullVideoForAnalysis(
	    @RequestParam String email,
	    @RequestParam String firstName,
	    @RequestParam String lastName,
	    @RequestParam String testName,
	    @RequestParam Integer attempt,
	    @RequestParam String companyId,
	    @RequestParam String videoPath) {

	    try {
	        System.out.println("Submit Video method entered");

	        VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	        session.setTestIdentifier(testName);
	        session.setVideoLink(videoPath);

	        String mergedFileName = Paths.get(videoPath).getFileName().toString();

		     
		     int index = videoPath.indexOf("/sessions/");
	
		     
		     String relativePath = (index != -1) ? videoPath.substring(index + "/sessions/".length()) : "";
	
		     
		     String fileUrl = config.getVideoFileServerBaseUrl() + relativePath;
		     
		     System.out.println("File URL = "+fileUrl);
	
		     session.setVideoUrl(fileUrl);

	        System.out.println("Saving session...");
	        session = rolePlayTestSessionService.saveOrUpdate(session);

	        if (session == null || session.getId() == null) {
	            System.err.println("Session not saved properly");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save session.");
	        }

	        System.out.println("Session saved with ID: " + session.getId());
	        
	        System.out.println("Sending video for insights....");
	
	        aSyncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithUnifiedPromptInAsyncDJ(
	            testName, email, companyId, session, videoPath
	        );

	        return ResponseEntity.ok("Video submitted and analysis started");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	    }
	}
	
	@PostMapping("/test-submit-video-google-models")
	public ResponseEntity<String> testModel(@RequestParam String location,
		    @RequestParam String model, @RequestParam String googleBucket, @RequestBody String prompt, @RequestParam String token) throws IOException{
		String videoInsights = geminiservice.videoInputWithGoogleCloudBucketUrl(location, model, prompt, googleBucket);
		return ResponseEntity.ok(videoInsights);
	}
	
	
	//videoLink stats with 'https' while googleBucketPath starts with 'gs..'
	@PostMapping("/submit-video-google")
	public ResponseEntity<String> submitGoogleFullVideoForAnalysis(
		@RequestParam String persona,
	    @RequestParam String email,
	    @RequestParam String firstName,
	    @RequestParam String lastName,
	    @RequestParam String testName,
	    @RequestParam Integer attempt,
	    @RequestParam String companyId,
	    @RequestParam String googleBucketPath, @RequestParam String token, @RequestParam(required = false) String location,
	    @RequestParam(required = false) String model, @RequestParam(required = false) String videoLink, @RequestParam(required = false) Long workflowSessionId) {
		System.out.println("in submit-video-google ");
		aSyncAIInsightsGenService.submitGoogleFullVideoForAnalysis(persona, email, firstName, lastName, testName, attempt, companyId, googleBucketPath, location, model, videoLink, workflowSessionId);
	    return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/submit-video-google-in-sync-mode")
	public ResponseEntity<String> submitGoogleFullVideoForAnalysisInSyncMode(
		@RequestParam String persona,
	    @RequestParam String email,
	    @RequestParam String firstName,
	    @RequestParam String lastName,
	    @RequestParam String testName,
	    @RequestParam Integer attempt,
	    @RequestParam String companyId,
	    @RequestParam String googleBucketPath, @RequestParam String token, @RequestParam(required = false) String location,
	    @RequestParam(required = false) String model, @RequestParam(required = false) String videoLink, @RequestParam(required = false) Long workflowSessionId) {
		System.out.println("in submit-video-google ****** "+googleBucketPath);
		googleBucketPath = URLDecoder.decode(googleBucketPath);
		System.out.println("in submit-video-google 2"+googleBucketPath);
		aSyncAIInsightsGenService.submitGoogleFullVideoForAnalysisSync(persona, email, firstName, lastName, testName, attempt, companyId, googleBucketPath, location, model, videoLink, workflowSessionId);
	    return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/fetch-roleplay-results")
	public ResponseEntity<VFRolePlayTestSession> fetchRolePlayResults(
	    @RequestParam String email,
	    @RequestParam String testName,
	    @RequestParam Integer attempt,
	    @RequestParam String companyId,
	    @RequestParam String token) {
		VFRolePlayTestSession session =  rolePlayTestSessionService.findVFRolePlayTestSessionByEmail(email, companyId, testName, attempt);
			if(session != null) {
				return ResponseEntity.ok(session);
			}
			return ResponseEntity.badRequest().build();
	}
	
}
