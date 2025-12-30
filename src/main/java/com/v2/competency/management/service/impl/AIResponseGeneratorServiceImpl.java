package com.v2.competency.management.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.googlecloud.vertex.ai.communication.dto.ExpectedResponseCommunication;
import com.googlecloud.vertex.ai.dto.ExpectedResponse2;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.insights.dto.InsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.service.VFTestService;

@Service
public class AIResponseGeneratorServiceImpl implements AIResponseGeneratorService{

	ObjectMapper mapper = new ObjectMapper();
	XmlMapper xmlMapper = new XmlMapper();
	
	@Autowired
	PropertyConfig config;
	
//	String projectId = "contactaiassessments";
//    String location = "asia-south1";
//    String modelName = "gemini-1.0-pro-vision";
    
    @Autowired
    VFTestService testService;
    
    ExpectedResponse2 response2 = new ExpectedResponse2();
    String json = null;
    
    ExpectedResponseCommunication responseCommunication = new ExpectedResponseCommunication();
    String jsonCommunication = null;
    
    @Autowired 
    QuestionService questionService;
    
    @Autowired
	GeminiAudioVideoService geminiservice;
	
	@Autowired
	RolePlayQuestionAnswerService service;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	
    
    @PostConstruct
    public void init() throws JsonProcessingException {
    	json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2);
    	jsonCommunication = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseCommunication);
    	mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }
    
    public String fetchUniqueSkillsForTestHashSeparated(VFTest test) {
    	String xml = test.getTestXml();
		CompetencyTest t  = null;
		Set<String> competencies = new HashSet<>();
		String uniqueSkills = "";
		try {
			t	= xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
			List<CompetencyQuestion> qs =  t.getCompetencies();
			for(CompetencyQuestion q : qs) {
				String str = "";
					if(q.getParentCompetency() == null || q.getParentCompetency().trim().length() == 0) {
						str = q.getCompetency();
					}
					else {
						str = q.getCompetency()+" in "+q.getParentCompetency();
					}
				competencies.add(str);
			}
			
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		for(String skill : competencies) {
			uniqueSkills += skill+"### ";
		}
		uniqueSkills = uniqueSkills.substring(0, uniqueSkills.lastIndexOf("###"));
		return uniqueSkills;
    }
    
    
    public String fetchUniqueSkillsForTest(VFTest test) {
    	String xml = test.getTestXml();
    		if(xml == null || xml.trim().length() == 0)
    		{
    			xml = test.getTestXmlForKB();
    		}
		CompetencyTest t  = null;
		Set<String> competencies = new HashSet<>();
		String uniqueSkills = "";
		try {
			t	= xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
			List<CompetencyQuestion> qs =  t.getCompetencies();
				if(qs !=null) {
					for(CompetencyQuestion q : qs) {
						String str = "";
							if(q.getParentCompetency() == null || q.getParentCompetency().trim().length() == 0) {
								str = q.getCompetency();
							}
							else {
								str = q.getCompetency()+" in "+q.getParentCompetency();
							}
						competencies.add(str);
					}
				}
				else {
					List<CompetencyDto> qs1 = t.getKbCompetencies();
					for(CompetencyDto q : qs1) {
						String str = "";
							if(q.getParentCompetency() == null || q.getParentCompetency().trim().length() == 0) {
								str = q.getCompetency();
							}
							else {
								str = q.getCompetency()+" in "+q.getParentCompetency();
							}
						competencies.add(str);
					}
				}
			
			
			
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		for(String skill : competencies) {
			uniqueSkills += skill+", ";
		}
		uniqueSkills = uniqueSkills.substring(0, uniqueSkills.lastIndexOf(","));
		return uniqueSkills;
    }
    
    public Set<String> fetchUniqueSkillsSetForTest(VFTest test) {
    	String xml = test.getTestXml();
		CompetencyTest t  = null;
		Set<String> competencies = new HashSet<>();
		
		try {
			t	= xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
			List<CompetencyQuestion> qs =  t.getCompetencies();
			for(CompetencyQuestion q : qs) {
				String str = "";
				if(q.getParentCompetency() == null || q.getParentCompetency().trim().length() == 0) {
					str = q.getCompetency();
				}
				else {
					str = q.getCompetency()+" in "+q.getParentCompetency();
				}
			competencies.add(str);
			}
			
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		
		return competencies;
    }
	
	@Override
	public ExpectedResponse2 generateAnalysisUsingGemini(List<VFTestUserQuestionAnswer> answers, String testIdentifier, String fullName, String companyId) throws RuntimeException {
		// TODO Auto-generated method stub
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
		String uniqueSkills = fetchUniqueSkillsForTest(test);
		String transcript = "Given below is the transcript of an interview focussed on skills in bracket - ("+uniqueSkills+") for  a candidate named "+fullName+System.lineSeparator();
		int count = 1;
		for(VFTestUserQuestionAnswer answer : answers) {
			//transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
			transcript += count+". "+answer.getQuestion()+System.lineSeparator();
			transcript +=  answer.getAnswer()+System.lineSeparator();
			count++;
		}
		transcript += "Can you analyse & score the answers on parameters included in following x format. Your score should be between 1 to 10 range. Please be critical in your analysis & scoring. If the length of the answer is not sufficient, assign as minimal score as possible. If the answer is not relevant to the question asked, reduce the score on other parameters. Also include a single summary json, having 'overAllObservationsForAllQuestions' and 'overAllFinalScore' parameters, in the response json for summing up candidate's performance across all parameters"+System.lineSeparator();
		transcript += json;
		System.out.println("json input "+json);
		return generate(transcript);
	}
	

	@Override
	public ExpectedResponseCommunication generateAnalysisCommunicationUsingGemini(
			List<VFTestUserQuestionAnswer> answers, String testIdentifier, String fullName, String companyId) {
		// TODO Auto-generated method stub
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
		String uniqueSkills = fetchUniqueSkillsForTest(test);
		String transcript = "Given below is the transcript of an interview focussed on skills in bracket - ("+uniqueSkills+") for  a candidate named "+fullName+System.lineSeparator();
		for(VFTestUserQuestionAnswer answer : answers) {
			transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
			transcript +=  answer.getAnswer()+System.lineSeparator();
		}
		transcript += "Can you analyse & score the answers on parameters included in following json format. Your score should be between 1 to 10 range. Please be critical in your analysis & scoring. If the length of the answer is not sufficient, assign as minimal score as possible. If the answer is not relevant to the question asked, reduce the score on other parameters. Also include a single summary json, having 'overAllObservationsForAllQuestions' and 'overAllFinalScore' parameters, in the response json for summing up candidate's performance across all parameters"+System.lineSeparator();
		transcript += json;
		System.out.println("json input "+json);
		return generateCommunication(transcript);
	}
	
	
	 private  ExpectedResponse2 generate(String transcript)
		      throws RuntimeException {
		    // Initialize client that will be used to send requests. This client only needs
		    // to be created once, and can be reused for multiple requests.
		    try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
		     // String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

		      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
		      
		  //    String interviewTransscript= FileUtils.readFileToString(new File("interview.txt"));
		      ExpectedResponse2 response2 = new ExpectedResponse2();
		      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2);
		      transcript += System.lineSeparator()+json;
		      GenerateContentResponse response = model.generateContent(
		    		  transcript
		      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		   System.out.println(res);
		  // List<VertexResponse> aiOutput =   mapper.readValue(res, new TypeReference<List<VertexResponse>>(){});
		   ExpectedResponse2 aiOutput = mapper.readValue(res, ExpectedResponse2.class);
		    
		    return aiOutput;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
		    	}
		  }
	 
	 private  InsightsDto generateCompetencyInsights(String transcript)
		      throws RuntimeException {
		    // Initialize client that will be used to send requests. This client only needs
		    // to be created once, and can be reused for multiple requests.
		    try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
		     // String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

		      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
		      
		  //    String interviewTransscript= FileUtils.readFileToString(new File("interview.txt"));
		      ExpectedResponse2 response2 = new ExpectedResponse2();
		      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2);
		      transcript += System.lineSeparator()+json;
		      GenerateContentResponse response = model.generateContent(
		    		  transcript
		      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		    res = res.replace("`", "");
		    res = res.replace('\u00A0',' ');
			  res = res.replace("'", " ");
			  mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			 
			  
			 res = res.trim();
			 if(res.startsWith("json")) {
				 res = res.substring("json".length(), res.length());
			 }
		   System.out.println(res);
		  // List<VertexResponse> aiOutput =   mapper.readValue(res, new TypeReference<List<VertexResponse>>(){});
		   InsightsDto aiOutput = mapper.readValue(res, InsightsDto.class);
		    
		    return aiOutput;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
		    	}
		  }
	 
	 private  String generateCompetencyInsightsForAnswer(String transcript)
		      throws RuntimeException {
		    // Initialize client that will be used to send requests. This client only needs
		    // to be created once, and can be reused for multiple requests.
		    try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
		     // String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

		      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
		      
		  //    String interviewTransscript= FileUtils.readFileToString(new File("interview.txt"));
		      ExpectedResponse2 response2 = new ExpectedResponse2();
		      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2);
		      transcript += System.lineSeparator()+json;
		      GenerateContentResponse response = model.generateContent(
		    		  transcript
		      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		   System.out.println(res);
		 return res;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
		    	}
		  }
	 
	 private  ExpectedResponseCommunication generateCommunication(String transcript)
		      throws RuntimeException {
		    // Initialize client that will be used to send requests. This client only needs
		    // to be created once, and can be reused for multiple requests.
		    try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
		     // String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

		      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
		      
		  //    String interviewTransscript= FileUtils.readFileToString(new File("interview.txt"));
		      ExpectedResponseCommunication response2 = new ExpectedResponseCommunication();
		      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2);
		      transcript += System.lineSeparator()+json;
		      GenerateContentResponse response = model.generateContent(
		    		  transcript
		      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		   System.out.println(res);
		  // List<VertexResponse> aiOutput =   mapper.readValue(res, new TypeReference<List<VertexResponse>>(){});
		   ExpectedResponseCommunication aiOutput = mapper.readValue(res, ExpectedResponseCommunication.class);
		    
		    return aiOutput;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
		    	}
		  }

	@Override
	public InsightsDto generateAnalysisUsingGeminiForCompetencyInsights(List<VFTestUserQuestionAnswer> answers,
			String testIdentifier, String fullName, String companyId) throws RuntimeException {
		try {
			VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
			String uniqueSkills = fetchUniqueSkillsForTest(test);
			String transcript = "Given below is the transcript of an interview focussed on skills in bracket - ("+uniqueSkills+") for  a candidate named "+fullName+System.lineSeparator();
			int count = 1;
			for(VFTestUserQuestionAnswer answer : answers) {
				//transcript += answer.getQid()+". "+answer.getQuestion()+System.lineSeparator();
				transcript += count+". "+answer.getQuestion()+System.lineSeparator();
				transcript +=  answer.getAnswer()+System.lineSeparator();
				count++;
			}
			transcript += "Can you analyse & score the answers on parameters included in following json format. Your score should be between 1 to 10 range. Please be critical in your analysis & scoring. If the length of the answer is not sufficient, assign as minimal score as possible. If the answer is not relevant to the question asked, reduce the score on other parameters. Also include a single summary json, having 'Overall Score' and 'Over All Insights' parameters, in the response json for summing up candidate's performance across all parameters"+System.lineSeparator();
			
			Set<String> parametersSet =   fetchUniqueSkillsSetForTest(test);
			 Map<String, Object> map = constructInsigntParamsMap(parametersSet);
			 InsightsDto dto = new InsightsDto();
			 dto.setParameters(map);
			String insightsJSON =  mapper.writerWithDefaultPrettyPrinter().withoutAttribute("insights").writeValueAsString(dto);
			transcript += insightsJSON;
			System.out.println("transcript with insightsJson \n"+transcript);
			return generateCompetencyInsights(transcript);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} 
	}

	
	private Map<String, Object> constructInsigntParamsMap(Set<String> parametersSet){
		 Map<String, Object> map = new HashMap<>();
		 for(String param : parametersSet) {
			 map.put("Score for "+param, 0);
			 map.put("Insights for "+param, "");
		 }
		 map.put("Overall Score", 0);
		 map.put("Over All Insights", "");
		 return map;
	}

	@Override
	public InsightForScenarioBasedQuestion generateAnalysisForScenarioBasedQuestion(VFTestUserQuestionAnswer answer) throws RuntimeException{
		// TODO Auto-generated method stub
		try {
			
			
			String competency = answer.getCompetency();
			String parentCompetency = answer.getParentCompetency();
			String transcript = "Given the scenario based Question for "+parentCompetency+" - "+competency+" in bracket - ("+answer.getQuestion()+") "+System.lineSeparator();
			transcript +="And given the response of the user below - "+System.lineSeparator();
			transcript += answer.getAnswer()+System.lineSeparator();
			transcript += "Can you strictly evaluate the user response to scenario on following parameters - Relevance, Problem Solving Skills, Decision Making Skills, Communication Skills, Ethical Considerations, Areas of Improvement & Over All Observations. Please respond using the following json format below"+System.lineSeparator();
			transcript += mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new InsightForScenarioBasedQuestion());
			String res = generateCompetencyInsightsForAnswer(transcript);
			
		  res = res.replace("`", "");
		    res = res.replace('\u00A0',' ');
			  res = res.replace("'", " ");
			 
			 
			  
			 res = res.trim();
			 if(res.startsWith("json")) {
				 res = res.substring("json".length(), res.length());
			 }
			
			InsightForScenarioBasedQuestion insights = mapper.readValue(res, InsightForScenarioBasedQuestion.class);
			return insights;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	

	@Override
	public String generateRolePlayAnalysisForRolePlayTest(String transcript) {
		 // to be created once, and can be reused for multiple requests.
	    try (VertexAI vertexAI = new VertexAI(config.getGeminiProjectId(), config.getGeminiLocation())) {
	     // String imageUri = "gs://cloud-samples-data/vertex-ai/llm/prompts/landmark1.png";

	      GenerativeModel model = new GenerativeModel(config.getGeminiModelName(), vertexAI);
	      

	      GenerateContentResponse response = model.generateContent(
	    		  transcript
	      );
	   
	    String res =  response.getCandidates(0).getContent().getParts(0).getText();
	    res = res.replace("`", "");
	    res = res.replace('\u00A0',' ');
		  res = res.replace("'", " ");
		
		 
		  
		 res = res.trim();
		 if(res.startsWith("json")) {
			 res = res.substring("json".length(), res.length());
		 }
	   System.out.println(res);
	
	  // RolePlayInsightsDto aiOutput = mapper.readValue(res, RolePlayInsightsDto.class);
	    
	    return res;
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    	throw new RuntimeException(e);
	    	}
	}

	@Override
	public String generateRolePlayAnalysisForRolePlayTestUsingAudioOrVideoLink(String audioOrVideoLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateAnalysisForScenarioBasedQuestionUsingAudioVideoLink(String question,
			MultipartFile audioOrVideoLink) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	

}
