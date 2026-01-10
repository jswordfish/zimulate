package com.v2.competency.management.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.dto.RelevancyScoreForAnswerDto;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.NewRolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.Section;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.SectionResult;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.entities.WorkflowRecommGenerator;
import com.v2.competency.management.repos.VFTestUserQuestionAnswerRepo;
import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.EmailService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.RelevancyCheckerService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.util.AIInsightsUtil;
@Service
@Transactional
public class ASyncAIInsightsGenServiceImpl implements ASyncAIInsightsGenService{
	@Autowired
	VFTestUserQuestionAnswerRepo questionAnswerRepo;
	
	@Autowired
	AIResponseGeneratorService aiResponseGeneratorService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService userCompetencyWiseScoreForAssessmentService;
	
	@Autowired
	RelevancyCheckerService relevancyCheckerService;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	GeminiAudioVideoService audioVideoService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	GeminiAudioVideoService geminiservice;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	WorkflowRecommGenerator workflowRecommGenerator;
	
	ObjectMapper mapper = new ObjectMapper();
	
	
	
	String rolePlayInsightsPrompt = "Given a transcript for a role play interview for Customer Service Executive below - "+System.lineSeparator()
			+ "${TRANSCRIPT} "+System.lineSeparator()
			+ "Can you analyse the transcript and evaluate on parameters - ${PARAMETERS}. Response of your Analysis should strictly be a json file based on instructions below."+System.lineSeparator()
			+"Post your analysis can you return the results json in following format BELOW?"
			+"${ANALYSIS_JSON}"+System.lineSeparator();
	
	String rolePlayInsightsPromptWithVideo = "Analyze this video and generate structured insights on the following parameters: " +
	           "\n1. **Confidence**: Rate the speaker's confidence level on a scale of 1-10." +
	           "\n2. **Fluency**: Assess speech flow and articulation." +
	           "\n3. **Accent**: Identify any prominent accents and evaluate clarity." +
	           "\n4. **Way of Speaking**: Describe the speaker’s tone, enthusiasm, and clarity." +
	           "\n5. **Body Language**: Evaluate gestures, posture, and facial expressions." +
	           "\n6. **Grammar**: Identify any grammatical errors or mispronunciations." +
	           "\n Provide the output in structured JSON format with scores for each category."+
	           "\n If the video does not contain any relevant speech (e.g., no speaker present, background noise only, or silent footage), return the JSON structure with a score of 0 for all parameters.";
	
	String unifiedRolePlayInsightsPrompt = 
		    "You are provided with a role play interview for a Customer Service Executive, which includes a **video recording** ." + System.lineSeparator() +
		    System.lineSeparator() +
		    "Analyze the candidate using the video on the following parameters:" + System.lineSeparator() +
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
			

	@Override
	@Async
	public void generateInsightsForScenarioBasedQsInAsync( String testName, Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs,
			String testIdentifier, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		System.out.println("In ASyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync start "+mapSubjectiveQs.size()+" "+mapSubjectiveQs);
		
		
		for(String key : mapSubjectiveQs.keySet()) {
			String comps[] = key.split("###");
			String parentComp = comps[0];
			String comp = comps[1];
			List<VFTestUserQuestionAnswer> list = mapSubjectiveQs.get(key);
			Float totalCompetencyWiseScore = 0f;
			Boolean evaluationFailed = false;
			String ids = "";
			
			
			
			for(VFTestUserQuestionAnswer ans : list) {
				ids += ans.getId()+",";
				System.out.println("In ASyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync ans id "+ans.getId());
			}
			for(VFTestUserQuestionAnswer ans : list) {
				System.out.println("In ASyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync id "+ans.getId());
				Boolean correct = true;
				if(ans.getQuestionType().equalsIgnoreCase(Question_Type.SUBJECTIVE.getType())) {
					
					
					try {
						VFTestUserQuestionAnswer answer2 = questionAnswerRepo.findById(ans.getId()).get();
						String qid = answer2.getQid();
						Question q =  questionService.findById(Long.parseLong(qid));
						RelevancyScoreForAnswerDto relevancy =  null;
						if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && ans.getAnswerAudioOrVideo() != null) {
//							String relPromp = AIInsightsUtil.generateAudioBasedRelevancyPompt(q);
//							String fileName = ans.getAnswerAudioOrVideo().substring(ans.getAnswerAudioOrVideo().lastIndexOf("/")+1, ans.getAnswerAudioOrVideo().length());
//							String baseLoc = config.getFileServerPath();
//							String loc = "";
//							 loc = baseLoc + java.io.File.separator + ans.getCompanyId() +java.io.File.separator + ans.getTestIdentifier() +File.separator+ans.getEmail()+File.separator+ans.getAttempt()+File.separator+ans.getQid();
//							
//							File file = new File(loc+File.separator+fileName);
//							String relevancyJson = audioVideoService.processAudioFile(relPromp, file);
//							relevancy = mapper.readValue(relevancyJson.getBytes(), RelevancyScoreForAnswerDto.class);
							
							
							String relPromp = AIInsightsUtil.generateAudioBasedRelevancyPompt(q);
							String fileName = ans.getAnswerAudioOrVideo().substring(ans.getAnswerAudioOrVideo().lastIndexOf("/")+1, ans.getAnswerAudioOrVideo().length());
							String baseLoc = config.getFileServerPath();
							String loc = "";
							 loc = baseLoc + java.io.File.separator + ans.getCompanyId() +java.io.File.separator + ans.getTestIdentifier() +File.separator+ans.getEmail()+File.separator+ans.getAttempt()+File.separator+ans.getQid();
							
							File file = new File(loc+File.separator+fileName);
							String relevancyJson = audioVideoService.processAudioFile("We are evaulating an audio response of a test taker to a scenario based text question. Question is '"+q.getQuestionText()+"'. Can you analyze the response and let me know if it is relevant enough?", file);
							System.out.println(" rel is "+relevancyJson);
							//relevancy = new RelevancyScoreForAnswerDto();
							relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
						}
						else {
							relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
						}
						InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = null;
						RolePlayInsightsDto customAISightsForScenarioBasedQuestion = null;
							if(relevancy.getRelevanceOfAnswerScoreInPercent() > 10) {
								
								if(q.getAiInsightsPrompt() != null && q.getAiInsightsPrompt().trim().length() > 0) {
									String transcriptWithExpectedJsonFormat = AIInsightsUtil.generateTranscriptBasedOnCustomPromptInQ(q, answer2.getAnswer());
									String customAISightsForScenarioBasedQuestionStringJson = null;
									if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && ans.getAnswerAudioOrVideo() != null) {
										System.out.println("analysing audio response 1");
										String fileName = ans.getAnswerAudioOrVideo().substring(ans.getAnswerAudioOrVideo().lastIndexOf("/")+1, ans.getAnswerAudioOrVideo().length());
										String baseLoc = config.getFileServerPath();
										String loc = "";
										 loc = baseLoc + java.io.File.separator + ans.getCompanyId() +java.io.File.separator + ans.getTestIdentifier() +File.separator+ans.getEmail()+File.separator+ans.getAttempt()+File.separator+ans.getQid();
										
										File file = new File(loc+File.separator+fileName);
										System.out.println("analysing audio response audio file "+file.getAbsolutePath());
										String audioPrompt = q.getAiInsightsPrompt();
										audioPrompt = AIInsightsUtil.generateAudioBasedTranscriptBasedOnCustomPromptInQ(q);
										//System.out.println("analysing audio response prompt "+audioPrompt);
										customAISightsForScenarioBasedQuestionStringJson = audioVideoService.processAudioFile(audioPrompt, file);
										System.out.println("analysing audio response done");
										System.out.println("____________");
										System.out.println(customAISightsForScenarioBasedQuestionStringJson);
										System.out.println("____________");
									}
									else {
										System.out.println("analysing text response 1");
										customAISightsForScenarioBasedQuestionStringJson = aiResponseGeneratorService.generateRolePlayAnalysisForRolePlayTest(transcriptWithExpectedJsonFormat);
									}
									 
									
									
									customAISightsForScenarioBasedQuestion = mapper.readValue(customAISightsForScenarioBasedQuestionStringJson.getBytes(), RolePlayInsightsDto.class);
									answer2.setCustomAiAnalysisJson(customAISightsForScenarioBasedQuestionStringJson);
									answer2.setAiInsightsGenerated(true);
									answer2.setOverallScoreIncaseOfSubjective(customAISightsForScenarioBasedQuestion.getOverAllScoreInPercent()*1.0f);
								}
								else {
									insightForScenarioBasedQuestion =  aiResponseGeneratorService.generateAnalysisForScenarioBasedQuestion(answer2);
									String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insightForScenarioBasedQuestion);
									answer2.setAiInsightsGenerated(true);
									answer2.setAiAnalysisJson(json);
									answer2.setOverallScoreIncaseOfSubjective(insightForScenarioBasedQuestion.getOverAllScoreInPErcentage());
								}
								
								
							}
							else {
								insightForScenarioBasedQuestion = new InsightForScenarioBasedQuestion();
								insightForScenarioBasedQuestion.setAnswerRelevanceToScenario(relevancy.getRelevancyOfAnswerObservations() );
								insightForScenarioBasedQuestion.setScoreForAnswerRelevanceToScenarioInPercentage(relevancy.getRelevanceOfAnswerScoreInPercent() *1.0f);
								insightForScenarioBasedQuestion.setOverAllScoreInPErcentage(0f);
								answer2.setOverallScoreIncaseOfSubjective(0f);
								String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(insightForScenarioBasedQuestion);
								answer2.setAiInsightsGenerated(true);
								answer2.setAiAnalysisJson(json);
							}
						
						questionAnswerRepo.save(answer2);
						if(insightForScenarioBasedQuestion !=null) {
							totalCompetencyWiseScore += insightForScenarioBasedQuestion.getOverAllScoreInPErcentage();
						}
						else {
							totalCompetencyWiseScore += customAISightsForScenarioBasedQuestion.getOverAllScoreInPercent();
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("in ASyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync "+e.getMessage());
						evaluationFailed = true; 
						break;
					}
				}
				
			}
			ids = ids.substring(0, ids.length() - 1);
			if(!evaluationFailed) {
				Float competencyWiseScore = totalCompetencyWiseScore / (list.size());
				
				createUserCompetencyWiseScoreForAssessmentRecord(testName, testIdentifier, email, companyId, attempt, parentComp, comp, competencyWiseScore, false, ids);
			}
			else {
				createUserCompetencyWiseScoreForAssessmentRecord(testName, testIdentifier, email, companyId, attempt, parentComp, comp, null, true, ids);
			}
			
			
		}
		System.out.println("In ASyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync end");
	}
	
	
	
//	private Integer checkIfAnswerRelevant(VFTestUserQuestionAnswer answer){
//		
//		String questionText = answer.getQuestion();
//		String answerTecxt = answer.getAnswer();
//		String parentCompetency = answer.getParentCompetency();
//		String competency = answer.getCompetency();
//		
//		String prompt = "...";
//		
//	} 
	
	
	
	
	private void createUserCompetencyWiseScoreForAssessmentRecord(String testName, String testIdentifier, String email, String companyId, Integer attempt, String parentComp, String comp, Float competencyWiseScore, Boolean evaluationFailed, String ansIds) {
		UserCompetencyWiseScoreForAssessment competencyWiseScoreForAssessment = UserCompetencyWiseScoreForAssessment.builder().competency(comp)
				.parentCompetency(parentComp)
				.testName(testName)
				.testIdentifier(testIdentifier)
				.averageScore(competencyWiseScore)
				.email(email)
				.attempt(attempt)
				.questionMode(Question_Type.SUBJECTIVE.getType())
				.evaluationFailed(evaluationFailed)
				.answerIds(ansIds)
				.build();
		competencyWiseScoreForAssessment.setCompanyId(companyId);
		userCompetencyWiseScoreForAssessmentService.addUserCompetencyWiseScoreForCompetency(competencyWiseScoreForAssessment);
	}

	@Override
	@Async
	public void generateInsightsForRolePlayBasedAssessmentInAsync(String testName, String email, String companyId,
			VFRolePlayTestSession rolePlayTestSession, String transcript) {
		try {
			rolePlayTestSession = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSession.getId());
			transcript = transcript.replace("\"", "");
			String input = rolePlayInsightsPrompt;
			input = input.replace("${TRANSCRIPT}", transcript);
			VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
			input = input.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
			String params[] = test.getCommaSeparatedAnalysisParams().split(",");
			RolePlayInsightsDto dto = new RolePlayInsightsDto();
				for(String param : params) {
					RoleplayInsightsDetail det = new RoleplayInsightsDetail();
					
					dto.getMapCompetenciesInsights().put(param, det);
				}
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
			input = input.replace("${ANALYSIS_JSON}", json);
			String insights =  aiResponseGeneratorService.generateRolePlayAnalysisForRolePlayTest(input);
			RolePlayInsightsDto res = mapper.readValue(insights.getBytes(), RolePlayInsightsDto.class);
			rolePlayTestSession.setInsightsJson(insights);
			rolePlayTestSession.setEvaluationFailed(false);
			rolePlayTestSession.setFinalScore(res.getOverAllScoreInPercent()*1.0f);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			rolePlayTestSession.setEvaluationFailed(true);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		}
	}
	
	@Override
	@Async
	public void generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(String testName, String email, String companyId, VFRolePlayTestSession rolePlayTestSession, String transcript, String mergedVideoPath) {
		try {
	        rolePlayTestSession = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSession.getId());
	        transcript = transcript.replace("\"", "");
	        
	        
	        String input = rolePlayInsightsPrompt.replace("${TRANSCRIPT}", transcript);
	        
	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
	        input = input.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
	        
	        String[] params = test.getCommaSeparatedAnalysisParams().split(",");
	        RolePlayInsightsDto dto = new RolePlayInsightsDto();
	        for (String param : params) {
	            RoleplayInsightsDetail det = new RoleplayInsightsDetail();
	            dto.getMapCompetenciesInsights().put(param, det);
	        }
	        
	        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
	        input = input.replace("${ANALYSIS_JSON}", json);
	        
	     
	        String transcriptInsights = aiResponseGeneratorService.generateRolePlayAnalysisForRolePlayTest(input);
	      //  RolePlayInsightsDto res = mapper.readValue(transcriptInsights.getBytes(), RolePlayInsightsDto.class);
	        
	        
	        
	        String[] videoParams = {"Confidence", "Fluency", "Accent", "Way of Speaking", "Body Language", "Grammar"};
	        RolePlayInsightsDto vidDto = new RolePlayInsightsDto();
	        
	        
	        for (String vidParam : videoParams) {
	            RoleplayInsightsDetail vidDet = new RoleplayInsightsDetail();
	            vidDto.getMapVideoInsights().put(vidParam, vidDet);
	        }
	        
	        String vidJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vidDto);
	        String vidInput = rolePlayInsightsPromptWithVideo.replace("${ANALYSIS_JSON}", vidJson);
	        
	        System.out.println("Sending video for insights | Path: " + mergedVideoPath);
	        
	        String videoInsights = audioVideoService.videoInput(vidInput, mergedVideoPath);
	
	        System.out.println("Video Insights for " + rolePlayTestSession.getId() + ": " + videoInsights);
	     
	        rolePlayTestSession.setInsightsJson(transcriptInsights);
	        rolePlayTestSession.setVideoInsightsJson(videoInsights);
//	        rolePlayTestSession.setEnergyGraphJson(videoEnergyJson);
	        rolePlayTestSession.setEvaluationFailed(false);
	       // rolePlayTestSession.setFinalScore(res.getOverAllScoreInPercent() * 1.0f);
	        System.out.println("Saving insights to DB for session: " + rolePlayTestSession.getId());
	        rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
	        System.out.println("Saved insights successfully for session: " + rolePlayTestSession.getId());
	       // System.out.println("Final Insights JSON: " + finalInsightsJson);

	    } catch (Exception e) {
	    	e.printStackTrace();
	        rolePlayTestSession.setEvaluationFailed(true);
	        rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
	    }
	}

	public static String convertHtmlToString(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        // Jsoup.parse(html) creates a DOM tree from the string.
        // .text() intelligently extracts all the text, preserving readable spacing.
        return Jsoup.parse(html).text();
    }

	@Override
	public void generateInsightsForRolePlayBasedAssessmentWithUnifiedPromptInAsyncDJ(String testName, String email,
			String companyId, VFRolePlayTestSession rolePlayTestSession, String mergedVideoPath) {
		// TODO Auto-generated method stub
		try {
			
			System.out.println("Insights generation logic trigerred");
			
	        rolePlayTestSession = rolePlayTestSessionService.findVFRolePlayTestSessionById(rolePlayTestSession.getId());
	        
	        
	        
	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
	        String input = unifiedRolePlayInsightsPrompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
	        String scenario = convertHtmlToString(test.getQuestionText());
	    	input = input.replace("${SCENARIO}", scenario);
	        
	        String[] params = test.getCommaSeparatedAnalysisParams().split(",");
	        RolePlayInsightsDto dto = new RolePlayInsightsDto();
	        for (String param : params) {
	            RoleplayInsightsDetail det = new RoleplayInsightsDetail();
	            dto.getMapCompetenciesInsights().put(param, det);
	        }
	        
	        String[] videoParams = {"Confidence", "Fluency", "Accent", "Way of Speaking", "Body Language", "Grammar"};
			
	        
	        RolePlayInsightsDto vidDto = new RolePlayInsightsDto();
	        for (String vidParam : videoParams) {
	        	RoleplayInsightsDetail vidDet = new RoleplayInsightsDetail();
	            vidDto.getMapVideoInsights().put(vidParam, vidDet);
	        }
	        
	        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
	        input = input.replace("${ANALYSIS_JSON}", json);
	        
	     
	        System.out.println("Sending video for insights | Path: " + mergedVideoPath);
	        
	        String videoInsights = audioVideoService.videoInput(input, mergedVideoPath);
	        
	        videoInsights = videoInsights.replaceFirst("```json\\n", "");
	        
	        // 2. Remove the trailing "\n```"
	        videoInsights = videoInsights.replaceFirst("\\n```", "");
	
	        System.out.println("Video Insights for " + rolePlayTestSession.getId() + ": " + videoInsights);
	        
	        if (videoInsights == null || videoInsights.trim().isEmpty()) {
	            System.err.println("No insights received from videoInput service!");
	        } else {
	            System.out.println("Received video insights: " + videoInsights);
	        }
	     
	        rolePlayTestSession.setInsightsJson(videoInsights);
	        rolePlayTestSession.setVideoInsightsJson(videoInsights);
	        
//	        rolePlayTestSession.setEnergyGraphJson(videoEnergyJson);
	        rolePlayTestSession.setEvaluationFailed(false);
	       // rolePlayTestSession.setFinalScore(res.getOverAllScoreInPercent() * 1.0f);
	        System.out.println("Saving insights to DB for session: " + rolePlayTestSession.getId());
	        rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
	        System.out.println("Saved insights successfully for session: " + rolePlayTestSession.getId());
	       // System.out.println("Final Insights JSON: " + finalInsightsJson);

	    } catch (Exception e) {
	    	e.printStackTrace();
	        rolePlayTestSession.setEvaluationFailed(true);
	        rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
	    }
	}
	
	private String getResultInputJson(VFRolePlayTest test) throws JsonProcessingException {
		System.out.println(" getResultInputJson ");
		if(test.getReportVersion() != null || test.getReportVersion().equalsIgnoreCase("V2")) {
			System.out.println(" getResultInputJson V2");
			NewRolePlayInsightsDto dto = new NewRolePlayInsightsDto();
			String[] params = test.getCommaSeparatedAnalysisParams().split(System.lineSeparator());
			System.out.println(" getResultInputJson params "+params.length);
			for(String line : params) {
				String terms[] = line.split("###");
				Section section = new Section(terms[0], terms[1]);
				System.out.println("section "+section.getHeader() +" - "+section.getDescription());
				System.out.println("terms 2 "+terms[2]);
				String[] p = terms[2].split(",");//right side of ###
				List<RoleplayInsightsDetail> list = new ArrayList<>();
					for(String ind : p) {
						RoleplayInsightsDetail detail = new RoleplayInsightsDetail();
						detail.setLabel(ind);
						list.add(detail);
					}
				SectionResult result = new SectionResult(section, list);
				dto.getSections().add(result);
			}
			
			Section section = new Section("Persuasive Presence", "These parameters are taken into consideration while assessing the Video");
			String[] videoParams = {"Confidence", "Fluency", "Accent", "Way of Speaking", "Body Language", "Grammar"};
			List<RoleplayInsightsDetail> list = new ArrayList<>();
		        for (String vidParam : videoParams) {
		            RoleplayInsightsDetail vidDet = new RoleplayInsightsDetail();
		            vidDet.setLabel(vidParam);
		            list.add(vidDet);
		        }
		        SectionResult result = new SectionResult(section, list);
				dto.getSections().add(result);
				System.out.println("dto "+dto.getSections().size());
				return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
		}
		else {
			System.out.println(" getResultInputJson Non V2");
			String[] params = test.getCommaSeparatedAnalysisParams().split(",");
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
	        
	        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
		}
		
	}
	
	@Override
	@Async
	public void submitGoogleFullVideoForAnalysis(String persona, String email, String firstName, String lastName, String testName,
			Integer attempt, String companyId, String googleBucketPath, String location, String model, String videoLink, Long workflowSessionId) {
		// TODO Auto-generated method stub
		try {
	        System.out.println("Submit Video method entered "+googleBucketPath);

	        VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	        session.setTestIdentifier(testName);
	        session.setVideoLink(googleBucketPath);
		    session.setVideoUrl(videoLink);
		    session.setRolePlayPersona(persona);
		    session.setWorkflowSessionId(workflowSessionId); // change here
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("Sending video for insights **** ");
	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
	        String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptEasy();
	        prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
	        prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
	        
	        
	        String json = getResultInputJson(test);
	        System.out.println("json is "+json);
	        prompt = prompt.replace("${ANALYSIS_JSON}", json);
	        String videoInsights = geminiservice.videoInputWithGoogleCloudBucketUrl(location==null?config.getGeminiLocation():location, model==null?config.getGeminiModelName():model, prompt, googleBucketPath);
	        videoInsights = videoInsights.replaceFirst("```json\\n", "");
	        videoInsights = videoInsights.replaceFirst("\\n```", "");
	        System.out.println("Recieved Video Insights for " + session.getId() + " & email " + email);
	        
	        if (videoInsights == null || videoInsights.trim().isEmpty()) {
	            System.err.println("No insights received from videoInput service!");
	        } else {
	            System.out.println("Received video insights: for "+email);
	        }
	     
	        session.setVideoInsightsJson(videoInsights);
	        session.setEvaluationFailed(false);
	        session.setReportsVersion(test.getReportVersion());
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("Saved insights successfully for session: " + session.getId());
	        String cc[] = {"jatin.sutaria@thev2technologies.com", "sales@zimulate.me"};
	        String subject = firstName+", Your Pitch Score for "+testName+" Role Play!!!";
	        emailService.sendEmail(email, cc, subject, testName, attempt, firstName, lastName, persona, companyId);
	        
	        ///Call work flow recomm gen services
	        if(workflowRecommGenerator.checkIfRecommCanBeGenerated(session.getId(), session.getWorkflowSessionId())) {
	        	workflowRecommGenerator.generateRecommendations(session.getWorkflowSessionId());
	        }
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


}
