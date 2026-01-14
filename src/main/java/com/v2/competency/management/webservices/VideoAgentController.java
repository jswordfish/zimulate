package com.v2.competency.management.webservices;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.workflow.insights.dto.LearningInsightsDetail;
import com.googlecloud.vertex.ai.workflow.insights.dto.Overall;
import com.googlecloud.vertex.ai.workflow.insights.dto.SkillCategory;
import com.v2.competency.management.dtos.AgentType;
import com.v2.competency.management.dtos.HeyGenKnowledgeBaseResponseDto;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.service.VideoAgentService;
import com.v2.competency.management.service.impl.VideoAgentServiceImpl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RestController
@CrossOrigin
public class VideoAgentController {

	
	private static final String API_KEY = "MTU4YmU1NmZjY2QwNGI5MmE4MDA4MmNhNWQxZDlhMDEtMTc1Nzc0ODM2MQ==";
	private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    private final String KNOWLEDGE_BASE_CREATION_API_HEYGEN_ENDPOINT = "https://api.heygen.com/v1/streaming/knowledge_base/create";
    //https://api.heygen.com/v1/streaming/knowledge_base/create
    
    @Autowired
    VideoAgentService agentService;
    
    Logger logger = LoggerFactory.getLogger(VideoAgentController.class);
    
    @Autowired
    VideoAgentMetaDataController videoAgentMetaDataController;
    
    @Autowired
    VideoAgentRepo videoAgentRepo;
	
	@RequestMapping(value="createVideoAgentUsingFileKB",method=RequestMethod.POST,
			consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })  
	@CrossOrigin
    public ResponseEntity<?> createVideoAgentUsingFileKB( @RequestParam MultipartFile file,  @RequestParam String companyId, @RequestParam String fileType,
    		@RequestParam MultipartFile agent,
            HttpSession session, @RequestParam String token) throws Exception{ 
		VideoAgent videoAgent =   objectMapper.readValue(agent.getInputStream(), VideoAgent.class);
		
		validate(videoAgent);
		HeyGenResponseStatus status = createKnowledgeBase(videoAgent.getName(), videoAgent.getIndustry(),  file, fileType, companyId, videoAgent.getOpeningStatement(), videoAgent.getPrompt());
		if(!status.isSuccess()) {
			return ResponseEntity.badRequest().body(status.getErrorDesc());
		}
		else {
			videoAgent.setKbId(status.getSuccessDesc().getData().getKnowledgeBaseId());
			agentService.saveOrUpdate(videoAgent);
			return ResponseEntity.ok(status);
		}
		
		
	}
	
	private String validate(VideoAgent videoAgent) {
			if(videoAgent.getAgentType() == null) {
				return "No Agent Type";
			}
			if(videoAgent.getCompanyId() == null) {
				return "No Company Id";
			}
			
			if(videoAgent.getName() == null) {
				return "No Name";
			}
			
			if(videoAgent.getObjective() == null) {
				return "No Objective";
			}
			
			if(videoAgent.getPrompt() == null) {
				return "No Prompt";
			}
			
			if(videoAgent.getOpeningStatement() == null) {
				return "No Opening Statement";
			}
		return "ok";
	}
	
	@RequestMapping(value="createVideoAgentUsingWebSiteUrlKB",method=RequestMethod.POST)  
	@CrossOrigin
	public ResponseEntity<?> createVideoAgentUsingWebSiteUrlKB( @RequestParam String websiteForKB,   @RequestParam String companyId, 
	    	  @RequestBody VideoAgent videoAgent,
	           HttpSession session, @RequestParam String token) throws Exception{ 
		String st = validate(videoAgent);
		if(!st.equalsIgnoreCase("ok")) {
			return ResponseEntity.badRequest().body(st);
		}
		String data = extractTextFromUrl(websiteForKB);
		HeyGenResponseStatus status = createKnowledgeBaseFromWebsite(videoAgent.getName(), videoAgent.getIndustry(), data, companyId, videoAgent.getOpeningStatement(), videoAgent.getPrompt());
		if(!status.isSuccess()) {
			return ResponseEntity.badRequest().body(status.getErrorDesc());
		}
		else {
			videoAgent.setKbId(status.getSuccessDesc().getData().getKnowledgeBaseId());
			agentService.saveOrUpdate(videoAgent);
			return ResponseEntity.ok(status);
		}
		
		
	}
	
	private HeyGenResponseStatus createKnowledgeBase(String name, String industry, MultipartFile file, String fileType, String companyId, String openingStatement, String prompt) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		String extractedText = extractTextFromFile(file, fileType);
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("name", name);
        payloadMap.put("description", "Knowledge base uploaded via API for company " + companyId);
        payloadMap.put("opening", openingStatement);
        // Truncate if necessary (HeyGen limit is roughly 20-40k chars depending on plan)
        payloadMap.put("prompt",  prompt+" --- \n\n" + extractedText);
        String jsonBody = objectMapper.writeValueAsString(payloadMap);
        // 3. Send to HeyGen API
       return sendToHeyGen(jsonBody);
	}
	
	private HeyGenResponseStatus createKnowledgeBase(String name, String industry, String contents, String companyId, String openingStatement, String prompt) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("name", name);
        payloadMap.put("description", "Knowledge base uploaded via API for company " + companyId);
        payloadMap.put("opening", openingStatement);
        // Truncate if necessary (HeyGen limit is roughly 20-40k chars depending on plan)
        payloadMap.put("prompt",  prompt+" --- \n\n" + contents);
        String jsonBody = objectMapper.writeValueAsString(payloadMap);
        // 3. Send to HeyGen API
       return sendToHeyGen(jsonBody);
	}
	
	@RequestMapping(value = "/searchVideoAgents", method = RequestMethod.GET)
	public ResponseEntity<?> searchVideoAgents( @RequestParam String token, @RequestParam String companyId, @RequestParam String search,
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) throws IOException{
		Pageable pageable = PageRequest.of(page, size);
		List<VideoAgent> agents = agentService.searchVideoAgents(search, companyId, pageable).getContent();
		return ResponseEntity.ok(agents);
	}
	
	@RequestMapping(value = "/fetchVideoAgentsByIds", method = RequestMethod.POST)
	public ResponseEntity<?> fetchVideoAgentsByIds( @RequestParam String token, @RequestParam String companyId, @RequestBody List<Long> ids
			) throws IOException{
		List<VideoAgent> agents = new ArrayList<>();
			for(Long id : ids) {
				agents.add(videoAgentRepo.findById(id).get());
			}
		return ResponseEntity.ok(agents);
	}
	
	private HeyGenResponseStatus createKnowledgeBaseFromWebsite(String name, String industry, String websiteScrappedData, String companyId, String openingStatement, String prompt) throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("name", industry+"-"+name);
        payloadMap.put("description", "Knowledge base uploaded via API for company " + companyId);
        payloadMap.put("opening", openingStatement);
        // Truncate if necessary (HeyGen limit is roughly 20-40k chars depending on plan)
        payloadMap.put("prompt",  prompt+" --- \n\nOther details - \n" + websiteScrappedData);
        String jsonBody = objectMapper.writeValueAsString(payloadMap);
        // 3. Send to HeyGen API
       return sendToHeyGen(jsonBody);
	}
	
	private HeyGenResponseStatus sendToHeyGen(String jsonBody) throws IOException, InterruptedException {
		 HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(KNOWLEDGE_BASE_CREATION_API_HEYGEN_ENDPOINT))
	                .header("Content-Type", "application/json")
	                .header("x-api-key", API_KEY) // Using the token passed in param
	                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	        	HeyGenKnowledgeBaseResponseDto res = objectMapper.readValue(response.body(), HeyGenKnowledgeBaseResponseDto.class);
	        	HeyGenResponseStatus status = HeyGenResponseStatus.builder().success(true).successDesc(res).build();
	        	return status;
	        } else {
	        	HeyGenResponseStatus status = HeyGenResponseStatus.builder().success(false).errorDesc(response.body()).build();
	        	return status;
	        }
	}
	
	/**
     * Helper method to parse different file types
     */
    private String extractTextFromFile(MultipartFile file, String fileType) throws IOException {
        String type = fileType.toLowerCase().trim();

        if (type.equals("pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } 
        else if (type.equals("word") || type.equals("docx")) {
            // Handles .docx files
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
                 XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                return extractor.getText();
            }
        } 
        else if (type.equals("text") || type.equals("txt")) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        throw new IllegalArgumentException("Unsupported file type: " + fileType);
    }
	
	
    private String extractTextFromUrl(String url) throws IOException {
        // 1. Validate URL format (basic check)
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        
        // 2. Connect and Get Document
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0") // Mimic a browser to avoid 403s
                .timeout(10000) // 10 second timeout
                .get();

        // 3. Extract visible text (strips HTML tags)
        String text = doc.body().text();
        
        return text;
    }
    
    
	public List<VideoAgent> generateDynamicVideoAgents(String overAll, String companyId) {
		try {
			List<VideoAgent> agentsTraining = new ArrayList<>();
			Overall insights = objectMapper.readValue(overAll.getBytes(), Overall.class);
			for(SkillCategory category:  insights.getSkillGapsByCategory()) {
				String skill = "";
				String kbContents = "";
				Integer count = 1;
				for( LearningInsightsDetail learning :  category.getSkillGapDetails()) {
					skill+= learning.getSkillGap()+",";
					kbContents+= count+". Training contents for "+learning.getSkillGap()+" - "+System.lineSeparator();
					kbContents += learning.getRecommendedLearningResources()+"."+System.lineSeparator();
					kbContents +=  "----------------------------------------";
					count++;
				}
				skill = skill.substring(0, skill.lastIndexOf(","));
				String openingStatement =  videoAgentMetaDataController.fetchOneOpeningStatementsForVideoAgent(AgentType.TRAINER.getType(), "Not applicable", skill);
				String prompt = videoAgentMetaDataController.fetchPromptTemplate_1_VariantForVideoAgentKnowledgebase(AgentType.TRAINER.getType(), skill);
				
				HeyGenResponseStatus heyGenResponseStatus =  createKnowledgeBase("Training on "+category.getCategory(), category.getCategory(), kbContents, companyId, openingStatement, prompt);
					if(heyGenResponseStatus.isSuccess()) {
						String objective = "The objective of this training is to help you master exactly what you need, right now. Based on your recent role-plays, we are a cerating a custom Video Agent designed specifically to help you strengthen your identified improvement areas ("+category.getCategory()+" - "+skill+").";
						VideoAgent videoAgent = VideoAgent.builder()
								.agentType(AgentType.TRAINER.getType())
								.company("General")
								.dynamicallyCreated(true)
								.industry(category.getCategory())
								.kbId(heyGenResponseStatus.getSuccessDesc().getData().getKnowledgeBaseId())
								.image("heygen_avatar_4.webp")
								.name("Training on "+category.getCategory())
								.objective(objective)
								.openingStatement(openingStatement)
								.prompt(prompt)
								.products("Identified Skill Gaps: "+category.getCategory()+" - "+skill)
								.build();
						videoAgent.setCompanyId(companyId);
						videoAgent = agentService.saveOrUpdate(videoAgent);
						agentsTraining.add(videoAgent);
					}
			}
			
			return agentsTraining;
		} catch (IOException e) {
			logger.error("Problems in converting Overall string to object", e);
			throw new RuntimeException(e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Problems in cerating HeyGen KB", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (Exception e) {
			logger.error("Problems in cerating HeyGen KB", e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
}

@Builder
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
class HeyGenResponseStatus{
	boolean success;
	
	String errorDesc;
	
	HeyGenKnowledgeBaseResponseDto successDesc;
	
}
