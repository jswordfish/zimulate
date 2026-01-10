package com.v2.competency.management;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.NewRolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.Section;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.SectionResult;
import com.googlecloud.vertex.ai.workflow.insights.dto.LearningInsightsDetail;
import com.googlecloud.vertex.ai.workflow.insights.dto.Overall;
import com.googlecloud.vertex.ai.workflow.insights.dto.SkillCategory;
import com.v2.competency.management.dtos.AgentType;
import com.v2.competency.management.dtos.GreetingDto;
import com.v2.competency.management.dtos.SalesPersona;
import com.v2.competency.management.dtos.WorkFlowDto;
import com.v2.competency.management.dtos.WorkflowAssignmentDto;
import com.v2.competency.management.dtos.WorkflowNodeDto;
import com.v2.competency.management.dtos.WorkflowNodeType;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VideoAgent;

public class TestWithoutAppContext2 {
	
ObjectMapper mapper = new ObjectMapper();
	
	XmlMapper xmlMapper = new XmlMapper();
	
	 
	
	@Test
	public void testJsonFromMap() throws JsonProcessingException {
		VFRolePlayTest rolePlayTest = VFRolePlayTest.builder()
				.parentCompetency("Sales & Business Development")
				.competency("Negotiation & Persuasion")
				.commaSeparatedAnalysisParams("Sales Acumen & Persuasion Skills, Product Knowledge, Problem-Solving Skills, Resilience in the face of rejection, Problem-Solving, Trust Building")
				.build();
		
		rolePlayTest.setTestName("Roleplay (Insurance) - Sell an Insurance Policy");
		rolePlayTest.setCompanyId("LTI");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rolePlayTest));
		
	}
	
	@Test
	public void testMarshalJson() throws IOException {
		String str = FileUtils.readFileToString(new File("dhruv.json"));
		RolePlayInsightsDto t = mapper.readValue(str, RolePlayInsightsDto.class);
		System.out.println(t);
	}
	
	@Test
	public void testGenerateGreetingJson() throws JsonProcessingException {
		GreetingDto greeting = GreetingDto.builder()
							.isStrictlyGreeting(false)
							.greetingResponse(null)
							.build();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(greeting));
	}
	
	@Test
	public void testListArray() throws JsonProcessingException {
		List<String> list = Arrays.asList("/Compose Test/v1.mp4", "/Compose Test/v2.mp4", "/Compose Test/v3.mp4", "/Compose Test/v4.mp4");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
	}
	
	@Test
	public void testGetPersonas() throws JsonProcessingException {
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(SalesPersona.Bargain_Hunter.getAllDtos()));
		
	}
	
	@Test
	public void testGeneratePrompt() throws JsonProcessingException {
		
		String prompt = "You are provided with a role play interview video for a Sales Executive appearing for a Insurance Sales man job. Sales Executive is trying to convince the recruiter (played by AI) of his Sales credentials.\n"
				+ "${SCENARIO}\n"
				+ "Analyze the candidate using the video on the following parameters:\n"
				+ "${PARAMETERS}.\n"
				+ "Here is additional guidance for evaluating the video:\n"
				+ "• **Confidence** – Rate the speaker's confidence level on a scale of 1–10.\n"
				+ "• **Fluency** – Assess speech flow and articulation.\n"
				+ "• **Accent** – Identify any prominent accents and evaluate clarity.\n"
				+ "• **Way of Speaking** – Describe the speaker’s tone, enthusiasm, and clarity.\n"
				+ "• **Body Language** – Evaluate gestures, posture, and facial expressions.\n"
				+ "• **Grammar** – Identify any grammatical errors or mispronunciations.\n"
				+ "Provide the output strictly in the following **structured JSON** format:\n"
				+ "${ANALYSIS_JSON}\n"
				+ "The observation/improvementAreas attributes in the json format above should be detailed and can potentially include actual text against the recorded audio/video to serve as examples to better illustrate a point.\n"
				+ "If the video does not contain any relevant speech (e.g., no speaker present, background noise only, or silent footage), return the JSON structure with a score of 0 for all parameters.\n"
				+ "";
		String questionText = "Your Role: As a  6 years experienced Life Insurance Sales Executive with AIA Singapore,  try pitching relevant Life Insurance product(s) to your Customer\n"
				+ "\n"
				+ "Make sure you listen to  your CLIENT NEEDS & his/her portfolio BEFORE pitching a relevant product.";
        prompt = prompt.replace("${SCENARIO}", questionText);
        String commaSeparatedAnalysisParams = "Rapport Building & Empathy, "+ "Establishing Credibility,"+ "Needs Discovery & Active Listening,"
        		+ "Domain Knowledge,"
        		+ "Value Proposition & Positioning,"
        		+ "Objection Handling, "
        		+ "Adaptability,"
        		+ "Closing Skills";
        prompt = prompt.replace("${PARAMETERS}", commaSeparatedAnalysisParams);
        String[] params = commaSeparatedAnalysisParams.split(",");
        RolePlayInsightsDto dto = new RolePlayInsightsDto();
        for (String param : params) {
            RoleplayInsightsDetail det = new RoleplayInsightsDetail();
            dto.getMapCompetenciesInsights().put(param, det);
        }
        String[] videoParams = {"Confidence", "Fluency", "Accent", "Way of Speaking", "Body Language", "Grammar"};
        for (String vidParam : videoParams) {
            RoleplayInsightsDetail vidDet = new RoleplayInsightsDetail();
            dto.getMapVideoInsights().put(vidParam, vidDet);
        }
        
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        prompt = prompt.replace("${ANALYSIS_JSON}", json);
        System.out.println(prompt);
	}
	
	@Test
	public void testGenerateNewResponseJson() throws JsonProcessingException {
		NewRolePlayInsightsDto dto = new NewRolePlayInsightsDto();
		Section perPresence = new Section("Persuasive Presence", "Expressive Range Analysis", "These parameters are taken into consideration while assessing the Video");
		Section conExp = new Section("Consultative Expertise", "These parameters are taken into consideration while assessing the test");
		Section compliance1 = new Section("Basic Financial Planning Awareness", "Quick Check on Financial Basics");
		
		RoleplayInsightsDetail detail1 = new RoleplayInsightsDetail();
		detail1.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail1.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail1.setScoreInPercent(50);
		detail1.setLabel("Fluency");
		
		RoleplayInsightsDetail detail2 = new RoleplayInsightsDetail();
		detail2.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail2.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail2.setScoreInPercent(50);
		detail2.setLabel("Fluency");
		
		RoleplayInsightsDetail detail3 = new RoleplayInsightsDetail();
		detail3.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail3.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail3.setScoreInPercent(50);
		detail3.setLabel("Fluency");
		
		RoleplayInsightsDetail detail4 = new RoleplayInsightsDetail();
		detail4.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail4.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail4.setScoreInPercent(50);
		detail4.setLabel("Fluency");
		
		RoleplayInsightsDetail detail5 = new RoleplayInsightsDetail();
		detail5.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail5.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail5.setScoreInPercent(50);
		detail5.setLabel("Fluency");
		
		RoleplayInsightsDetail detail6 = new RoleplayInsightsDetail();
		detail6.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail6.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail6.setScoreInPercent(50);
		detail6.setLabel("Fluency");
		
		RoleplayInsightsDetail detail7 = new RoleplayInsightsDetail();
		detail7.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail7.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail7.setScoreInPercent(50);
		detail7.setLabel("Fluency");
		
		RoleplayInsightsDetail detail8 = new RoleplayInsightsDetail();
		detail8.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail8.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail8.setScoreInPercent(50);
		detail8.setLabel("Fluency");
		
		RoleplayInsightsDetail detail9 = new RoleplayInsightsDetail();
		detail9.setImprovementAreas("The candidate must practice speaking clearly and articulately. Engaging in mock calls and public speaking exercises can help improve speech flow and confidence in communication.");
		detail9.setObservation("The candidate barely spoke. He only uttered 'Hi, hello' (00:14) a couple of times. Due to the minimal speech, fluency could not be properly assessed, but the reluctance to speak is a major concern.");
		detail9.setScoreInPercent(50);
		detail9.setLabel("Fluency");
		
		SectionResult result1 = new SectionResult(perPresence, Arrays.asList(detail1, detail2, detail3));
		SectionResult result2 = new SectionResult(conExp, Arrays.asList(detail4, detail5, detail6));
		SectionResult result3 = new SectionResult(conExp, Arrays.asList(detail7, detail8, detail9));
		
		dto.setSections(Arrays.asList(result1, result2, result3));
		dto.setOverAllObservation("The candidate completely failed to perform the assigned role-play. He was silent, unresponsive, and disengaged throughout the entire interaction, despite multiple prompts from the customer (AI bot). He demonstrated no product knowledge, sales skills, or professionalism. His body language, tone, and the unprofessional setting all contributed to an extremely poor performance. The candidate appeared completely unprepared for the interview.");
		dto.setOverAllScore(30);
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
		
	}
	
	@Test
	public void testVideoAgentCreation() throws IOException {
		String prompt = FileUtils.readFileToString(new File("prompt_eg.txt"));
		VideoAgent agent =   VideoAgent.builder()
		.agentType("Sales")
		.industry("Insurance")
		.name("Onboarding Agent ICICI")
		.objective("To inform first time visitors on ICICI term insurance plans")
		.openingStatement("Hello, I am calling from ICiCI and calling you in response to your inquiry ?")
		.prompt(prompt)
		.build();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(agent));
		
	}
	
	@Test
	public void testVideoAgentTypeJsonGen() throws IOException {
		List<String> list =  AgentType.EMPLOYEE_ONBOARDING.getAllAgents();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
		
	}
	
//	@Test
//	public void testCreateWorkflowJsonstructure() throws JsonProcessingException {
//		Workflow workflow = Workflow.builder().workflowName("Zimulate Training Flow").build();
//		
//		WorkflowNode node1 = WorkflowNode.builder().companyId("LTI").nodeName("Video Agent Training on Zimulate Platform")
//				.nodeType("Video Agent")
//				.position(1)
//				.build();
//		
//		WorkflowNode node2 = WorkflowNode.builder().companyId("LTI").nodeName("Zimulate Roleplay Training on Zimulate Platform")
//				.nodeType("Zimulate Training Roleplay")
//				.position(2)
//				.build();
//		
//		WorkflowNode node3 = WorkflowNode.builder().companyId("LTI").nodeName("Zimulate Roleplay Pitching Zimulate Platform")
//				.nodeType("Zimulate Assessment Roleplay")
//				.position(3)
//				.build();
//		
//		WorkflowNode node4 = WorkflowNode.builder().companyId("LTI").nodeName("Show Results")
//				.nodeType("Zimulate Roleplay Results")
//				.position(4)
//				.build();
//		
//		WorkflowNode node5 = WorkflowNode.builder().companyId("LTI").nodeName("Show Learning Recommendations")
//				.nodeType("Zimulate Learning Recommendations")
//				.position(5)
//				.build();
//		
//		WorkflowNode node6 = WorkflowNode.builder().companyId("LTI").nodeName("Show Dynamic Video Agent on Learning Recomm")
//				.nodeType("Zimulate Assessment Roleplay Dynamic")
//				.position(5)
//				.build();
//		
//		List<WorkflowNode> list =  Arrays.asList(node1, node2, node3, node4, node5, node6);
//		workflow.setList(list);
//		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(workflow));
//		
//		
//	}
	
	@Test
	public void testActualWorkFlowJPA() throws JsonProcessingException {
		
		WorkFlowDto workflow = WorkFlowDto.builder()
				.industry("Insurance")
				.name("Onboarding Term Policy Sales Agents")
				.objective("NA")
				.build();
		
		workflow.setCompanyId("LTI");
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(workflow));
		
		
		WorkflowNodeDto dto = WorkflowNodeDto.builder().position(1)
				.videoAgentTrainingId(10120l)
				.type(WorkflowNodeType.VIDEO_AGENT_TRAINING.getWorkflowNodeType())
				.workFlowId(10137l)
				.build();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
		
	}
	
	@Test
	public void testWorkflowAssignmentDto() throws JsonProcessingException {
		WorkflowAssignmentDto dto = WorkflowAssignmentDto.builder()
				.emails(Arrays.asList("jatin.sutaria@thev2technologies.com"))
				.workflowIds(Arrays.asList(10213l))
				.build();
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
		
	}
	
	@Test
	public void testWorkflowSkillGapFindingDto() throws JsonProcessingException {
		Overall dto = Overall.builder().analysisSummary("A brief 1-sentence summary of the user's overall performance.")
				.skillGapsByCategory(new ArrayList<SkillCategory>())
				.build();
		SkillCategory category1 = SkillCategory.builder()
				.category("Name of the High-Level Category (e.g., Negotiation Skills)")
				.reasoning("Why was this category selected?")
				.skillGapDetails(new ArrayList<>())
				.build();
		
		LearningInsightsDetail detail1 = LearningInsightsDetail.builder()
				.skillGap("Handling Objections")
				.recommendedLearningResources("Topic 1 for the Video Agent to teach")
				.build();
		LearningInsightsDetail detail2 = LearningInsightsDetail.builder()
				.skillGap("Pricing Strategy")
				.recommendedLearningResources("Topic 2 for the Video Agent to teach")
				.build();
		
		category1.getSkillGapDetails().add(detail1);
		category1.getSkillGapDetails().add(detail2);
		
		dto.getSkillGapsByCategory().add(category1);
		
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto));
		
	}
	
	@Test
	public void testGenUrlOnFly() {
		String base = "https://zimulate.me/roleplayresult?companyId=";
		
		String resultPage = base+"LTI"+"&email="+URLEncoder.encode("sales@zimulate.me")+"&roleplay="
		+URLEncoder.encode("Roleplay - Clarity & Transparency: Full Product Disclosure")+"&attempt="+"1"
		+"&firstName="+URLEncoder.encode("Test")+"&lastName="+URLEncoder.encode("Last");
		System.out.println(resultPage);

	}

}
