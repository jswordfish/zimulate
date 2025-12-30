package com.v2.competency.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.dto.ExpectedResponse2;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.dtos.AIObjectiveQuestion;
import com.v2.competency.management.dtos.AIQuestionSet;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.Proficiency;
import com.v2.competency.management.dtos.VoiceflowPayload;
import com.v2.competency.management.entities.Question_Source;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public class TestWithoutAppContext {
	
	ObjectMapper mapper = new ObjectMapper();
	
	XmlMapper xmlMapper = new XmlMapper();
	
	@Test
	public void testJsonFromMap() throws JsonProcessingException {
		Map<String,Object> map = new HashMap<>();
		map.put("Numerical Reasoning", 0);
		map.put("Verbal Reasoning", 0);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
		
	}
	
	@Test
	public void testCreatePayload() throws JsonProcessingException {
		VoiceflowPayload payload  = new VoiceflowPayload();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));
		
	}
	
	@Test
	public void testCreateAIAnalysisResponsePayload() throws JsonProcessingException {
		ExpectedResponse2 response2 = new ExpectedResponse2();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response2));
		
	}
	
	
	@Test
	public void testCreateNestedPayload() throws IOException {
		
		CompetencyQuestion question1 = CompetencyQuestion.builder().competency("Quality of Introduction")
							.parentCompetency("English Language")
							.weightOfQuestion(1)
							.qid("0")
							.proficiencyLevel(Proficiency.LEVEL3.getLevel())
							.evaluationParameters("Evaluate the user answer on following 4 parameters - Relevance to Question, Grammar, Depth of the Answer, Vocabulary")
							.build();
		
		CompetencyQuestion question2 = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Technical Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question2_followup = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Technical Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		question2.setFollowups(new ArrayList<>());
		question2.getFollowups().add(question2_followup);
		
		CompetencyQuestion question3 = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Content Management Systems (CMS)")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question4 = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Microsoft Office Suite")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question5 = CompetencyQuestion.builder().competency("Excellent Time Management")
				.parentCompetency("Organizational & Communication Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question6 = CompetencyQuestion.builder().competency("Excellent Time Management")
				.parentCompetency("Organizational & Communication Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question6_followup= CompetencyQuestion.builder().competency("Excellent Time Management")
				.parentCompetency("Organizational & Communication Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		question6.setFollowups(new ArrayList<>());
		question6.getFollowups().add(question6_followup);
		
		CompetencyQuestion question6_followup_followup= CompetencyQuestion.builder().competency("Excellent Time Management")
				.parentCompetency("Organizational & Communication Skills")
				.weightOfQuestion(1)
				.qid("0")
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		question6_followup.setFollowups(new ArrayList<>());
		question6_followup.getFollowups().add(question6_followup_followup);
		
		CompetencyTest competencyTest = CompetencyTest.builder()
				.competencies(Arrays.asList(question1, question2, question3, question4, question5, question6))
				.build();
		
		
		Integer seq = 1;
		String id = "";
		for(CompetencyQuestion question : competencyTest.getCompetencies()) {
			id = ""+seq;
			question.setQid(id);
			Integer followup_seq = 1;
			for(CompetencyQuestion followup : question.getFollowups()) {
				id += ""+followup_seq;
				followup.setQid(id);
				Integer followup_to_followup_seq = 1;
				for(CompetencyQuestion followupToFollowUp : followup.getFollowups()) {
					id += ""+followup_to_followup_seq;
					followupToFollowUp.setQid(id);
					followup_to_followup_seq++;
					if(followupToFollowUp.getFollowups() != null && followupToFollowUp.getFollowups().size() > 0) {
						throw new RuntimeException("Can not have more than 2 nested followups");
						
					}
				}
				followup_seq++;
			}
			seq++;
		}
			
		String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest);
		//System.out.println(xml);
		
		FileUtils.write(new File("competency_test.xml"), xml);
		
		CompetencyTest test2 = xmlMapper.readValue(new File("competency_test.xml"), CompetencyTest.class);
	//	System.out.println(test2);
		
		VFTest vfTest = VFTest.builder().testIdentifier("10001").build();
		//vfTest.setTestXml(xml);
		vfTest.setCompanyId("t01");
		vfTest.setTestIdentifier("test "+System.currentTimeMillis());
		vfTest.setCompetencyTest(competencyTest);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vfTest));
	}
	
	@Test
	public void createVFTestUserQuestionAnswerJson() throws JsonProcessingException {
		VFTestUserQuestionAnswer answer = VFTestUserQuestionAnswer.builder()
				.firstName("abc")
				.last(false)
				.lastName("sutaria")
				.email("abc@def.com")
				.testIdentifier("10001")
				.question("Q")
				.answer("A")
				.build();
		
		answer.setCompanyId("t01");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(answer));
	}
	
	@Test
	public void testCreateInputCompetenciesJson() throws Exception {
		CompetencyQuestion question3 = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Content Management Systems (CMS)")
				.weightOfQuestion(1)
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question4 = CompetencyQuestion.builder().competency("Proficiency in Marketing Tools")
				.parentCompetency("Microsoft Office Suite")
				.weightOfQuestion(2)
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		
		CompetencyQuestion question5 = CompetencyQuestion.builder().competency("Excellent Time Management")
				.parentCompetency("Organizational & Communication Skills")
				.weightOfQuestion(1)
				.proficiencyLevel(Proficiency.LEVEL3.getLevel())
				.build();
		List<CompetencyQuestion> list = Arrays.asList(question3 ,question4, question5);
		
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
		
	}
	
	@Test
	public void testGetJsonForQuestionSet() throws JsonProcessingException {
		AIQuestionSet set = AIQuestionSet.builder().build();
		AIObjectiveQuestion q1 = AIObjectiveQuestion.builder().question("Sample Question 1")
				.choice1("This is Choice 1")
				.choice2("This is Choice 2")
				.choice3("This is Choice 3")
				.choice4("This is Choice 4")
				.correctChoices("Choice 1")
				.build();
		set.getQuestions().add(q1);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(set));
	}
	
	@Test
	public void testCreateBF_KBTestJson() throws JsonProcessingException {
		VFTest test = VFTest.builder().build();
		test.setQuestionSource(Question_Source.QUESTION_BANK_RANDOM.getSource());
		CompetencyTest competencyTest = CompetencyTest.builder().build();
		
		CompetencyDto dto1 = CompetencyDto.builder()
				.competency("Proficiency in Marketing Tools")
				.parentCompetency("Technical Skills")
				.noOfQuestionsToBeAsked(5)
				.questionType(Question_Type.MCQ.getType())
				.build();
		
		List<CompetencyDto> list =   Arrays.asList(dto1);
		competencyTest.setKbCompetencies(list);
		test.setCompetencyTest(competencyTest);
		test.setCompanyId("t01");
		test.setDuration(30);
		test.setTestName("KB Test 1");
		test.setTestIdentifier("10001");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest));
	}
	
	@Test
	public void testVFTestUserQuestionAnswerJson() throws JsonProcessingException {
		long t = System.currentTimeMillis();
		VFTestUserQuestionAnswer answer = VFTestUserQuestionAnswer.builder()
							.answer("Sample ")
							.competency("Comp 1")
							.email("test@etst.com")
							.firstName("John")
							.lastName("Smith")
							.parentCompetency("Parent Comp 1")
							.qid("1")
							.question("Test Q")
							.questionType(Question_Type.SURVEY.getType())
							.testIdentifier("10001")
							.timeTakenToAnswerInMinutes(2)
							.timeOfLastQuestion(t)
							.build();
		answer.setCompanyId("t01");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(answer));
	}
		
	@Test
	public void testCreateJsonForScenarioBasedDto() throws JsonProcessingException {
		InsightForScenarioBasedQuestion q = new InsightForScenarioBasedQuestion();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(q));
		
	}
	@Test
	public void testReadJson() throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		String xml = FileUtils.readFileToString(new File("delete.json"));
		CompetencyTest compTest =  objectMapper.readValue(xml.getBytes(), CompetencyTest.class);
 		System.out.println(compTest);
	}
	
	
	
	@Test
	public void testCheckRecieveVideoAPIStreaming() {
		String videoFilePath = "Dhruv.mp4";
//        String apiUrl = "https://myac.ai:99/receive-video?email=jatin.sutaria5%40thev2technologies.com&firstName=Jatin2&lastName=Sutaria1&testName=Role%20Play%20-%20Customer%20Service%20-%20Complaint%20Handling&attempt=1&companyId=LTI&isFinal=false"
//        		+ "";
		String apiUrl = "http://localhost:8090/receive-video?email=jatin.sutaria6%40thev2technologies.com&firstName=Jatin2&lastName=Sutaria1&testName=Role%20Play%20-%20Customer%20Service%20-%20Complaint%20Handling&attempt=1&companyId=LTI&isFinal=false"
        		+ "";
        int chunkSize = 1024 * 1024; // 1MB chunk size
        String sessionId = null;
        boolean isFirstChunk = true;

        File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            System.err.println("Video file not found: " + videoFilePath);
            return;
        }

        try (InputStream fileInputStream = new FileInputStream(videoFile)) {
            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = videoFile.length();
            int count = 1;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            	if(count == 2) {
            		apiUrl +="&sessionId="+URLEncoder.encode(sessionId);
            	}
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setChunkedStreamingMode(0); // Let the HTTP client handle chunking if supported

                

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                int responseCode = connection.getResponseCode();
                String httpResponseMessage = connection.getResponseMessage();

                // Read the response
                StringBuilder response = new StringBuilder();
                try (InputStream responseStream = connection.getInputStream()) {
                    byte[] responseBuffer = new byte[1024];
                    int responseBytesRead;
                    while ((responseBytesRead = responseStream.read(responseBuffer)) != -1) {
                    	response.append(new String(responseBuffer, 0, responseBytesRead, StandardCharsets.UTF_8));
                    }
                }

                System.out.println("Response Code: " + httpResponseMessage);
                System.out.println("Response Message: " + response.toString());
                

                if (responseCode == HttpURLConnection.HTTP_OK) {
                	String res = response.toString();
                	if(!res.equalsIgnoreCase("Ok")) {
                		sessionId = res;
                        System.out.println("Received Session ID: " + sessionId);
                	}
                	else {
                		break;
                	}
                	
                }

                totalBytesRead += bytesRead;
                
                
                

                connection.disconnect();
                	count++;
                // Simulate some delay between chunks (optional)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (Exception e) {
            System.err.println("Error during video streaming: " + e.getMessage());
        }
	}
}
