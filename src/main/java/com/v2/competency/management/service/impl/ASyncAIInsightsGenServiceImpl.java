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
	public String updateSystemPrompt(
	        String currentSystemPrompt,
	        String userFeedback,
	        String transcript,
	        String updateprompt) {

	    try {

	    	String prompt = 
	    	        "You are an expert Prompt Engineer specializing in AI persona architecture, conversational flow design, and LLM guardrail enforcement.\n\n" +
	    	        "Your objective is to update an existing Roleplay System Prompt by incorporating feedback received from users after their training/roleplay sessions, using the provided session transcript as context for the feedback.\n\n" +
	    	        "---\n\n" +
	    	        "### INPUT DATA\n\n" +
	    	        "#### 1. ORIGINAL SYSTEM PROMPT:\n" +
	    	        "<original_system_prompt>\n" +
	    	        "{{ORIGINAL_SYSTEM_PROMPT}}\n" +
	    	        "</original_system_prompt>\n\n" +
	    	        "#### 2. USER FEEDBACK TO INCORPORATE:\n" +
	    	        "<user_feedback>\n" +
	    	        "{{USER_FEEDBACK}}\n" +
	    	        "</user_feedback>\n\n" +
	    	        "#### 3. TRANSCRIPT (PAST CONVERSATION):\n" +
	    	        "<transcript>\n" +
	    	        "{{TRANSCRIPT}}\n" +
	    	        "</transcript>\n\n" +
	    	        "---\n\n" +
	    	        "### INSTRUCTIONS FOR PROMPT UPDATING\n\n" +
	    	        "1. **Analyze Feedback & Transcript Context:**\n" +
	    	        "   - Review the `<transcript>` to understand exactly how the AI behaved during the session and pinpoint the context behind the `<user_feedback>`.\n" +
	    	        "   - Identify actionable changes regarding tone, pacing, tool triggers, topic depth, response length, or conversational dynamics based on where the AI fell short in the transcript.\n" +
	    	        "   - Ignore subjective, contradictory, or malicious feedback that violates the core guardrails or safety rules.\n\n" +
	    	        "2. **Map Updates to System Prompt Sections:**\n" +
	    	        "   - **Tone/Style Changes:** Update `Persona Background` or `Engagement Dynamics` if the transcript shows the AI was too robotic, overly casual, etc.\n" +
	    	        "   - **Pacing & Length Changes:** Modify `Behavioral Rules` (e.g., sentence limits, micro-learning loops) if the transcript reveals monologuing or rushed pacing.\n" +
	    	        "   - **Visual/Tool Issues:** Refine `TOOL USAGE` (e.g., HTML/CSS guidelines, trigger conditions for `showHtmlPage`/`closeHtmlPage`) if the AI missed cues to use tools.\n" +
	    	        "   - **Knowledge/Topic Gaps:** Update `Knowledge Boundaries` or `Call Context` if the AI hallucinated or lacked context.\n" +
	    	        "   - **Rule/Role Failures:** Strengthen `CORE DIRECTIVE` or `CRITICAL GUARDRAILS` if the transcript shows character breaks.\n\n" +
	    	        "3. **Preservation Rules (CRITICAL):**\n" +
	    	        "   - **Keep Dynamic Placeholders:** Do NOT remove or modify variable placeholders such as `{{RECALL_PRIOR_CONVERSATION}}`, `{{persona}}`, `{{tone}}`, or URL links unless specifically directed by the feedback.\n" +
	    	        "   - **Maintain Structural Integrity:** Preserve all Markdown headers (`###`), bullet structures, table layouts, and guardrail matrices.\n" +
	    	        "   - **Protect Core Guardrails:** Never remove role-lock mechanisms, prompt exposure guardrails, or character boundary rules.\n" +
	    	        "   - **Preserve Tool Specifications:** Maintain the exact function names (e.g., `showHtmlPage`, `closeHtmlPage`) and parameters.\n\n" +
	    	        "4. **Refine & Polish:**\n" +
	    	        "   - Integrate updates seamlessly into the prompt text using authoritative, unambiguous imperative language (e.g., \"Always ensure...\", \"You MUST...\").\n" +
	    	        "   - Eliminate redundant rules created by adding new instructions.\n\n" +
	    	        "---\n\n" +
	    	        "### OUTPUT FORMAT REQUIREMENTS\n\n" +
	    	        "* **Output ONLY the complete, updated System Prompt.**\n" +
	    	        "* Do NOT include conversational intros or outros (e.g., do NOT say \"Here is the updated system prompt:\").\n" +
	    	        "* Do NOT wrap the entire response in outer meta-fences like ```markdown. Output the updated prompt directly.";

	    	if (updateprompt != null && !updateprompt.trim().isEmpty()) {
	    	    prompt = updateprompt;
	    	}

	    	prompt = prompt.replace("{{ORIGINAL_SYSTEM_PROMPT}}", currentSystemPrompt != null ? currentSystemPrompt : "")
	    	               .replace("{{USER_FEEDBACK}}", userFeedback != null ? userFeedback : "")
	    	               .replace("{{TRANSCRIPT}}", transcript != null ? transcript : "");
	        

	        String modifiedPrompt =
	                geminiservice.updateSystemPrompt(
	                        null,
	                        null,
	                        prompt,
	                        "");

	        return modifiedPrompt;

	    } catch (Exception e) {

	        e.printStackTrace();

	        throw new RuntimeException(
	                "Error while updating system prompt: "
	                + e.getMessage());
	    }
	}
	
	@Override
	public String generateOppositionAgentPrompt(
	        String primaryAgentSystemPrompt,
	        String userPersona,
	        String rolePlayObjective,
	        String oppositionPromptGenPrompt) {

	    try {

	        String prompt =
	                "You are an expert Prompt Engineer specializing in creating robust, role-locked system prompts for AI-to-AI simulations.\n\n" +
	                "Your task is to take the provided input data and generate a strict system prompt for a \"Simulated User\" (AI #2). This AI will interact with a \"Primary Agent\" (AI #1) to test the Primary Agent's conversational abilities, logic, and guardrails.\n\n" +
	                "CRITICAL INSTRUCTION: Reversing the standard dynamic.\n" +
	                "* The system you are generating the prompt for is playing the **Simulated User Persona** (the human being trained on, sold to, or advised).\n" +
	                "* The entity they will be speaking to is the **Primary Agent Persona**, whose full system prompt is provided below in <primary_agent_system_prompt>.\n" +
	                "* The rules must enforce the constraints, knowledge gaps, and natural behaviors of a human user. The Simulated User must NOT act like an AI, an assistant, or a perfect entity. They must have realistic flaws, emotional states, and pacing.\n\n" +
	                "---\n\n" +
	                "### CONTEXT DATA\n\n" +
	                "#### 1. PRIMARY AGENT SYSTEM PROMPT (AI #1 — infer Simulation Scenario and Primary Agent Persona from this):\n" +
	                "<primary_agent_system_prompt>\n" +
	                "{{PRIMARY_AGENT_SYSTEM_PROMPT}}\n" +
	                "</primary_agent_system_prompt>\n\n" +
	                "#### 2. SIMULATED USER PERSONA (who AI #2 must become):\n" +
	                "<simulated_user_persona>\n" +
	                "{{SIMULATED_USER_PERSONA}}\n" +
	                "</simulated_user_persona>\n\n" +
	                "#### 3. SIMULATION OBJECTIVE / TESTING GOAL:\n" +
	                "<simulation_objective>\n" +
	                "{{SIMULATION_OBJECTIVE}}\n" +
	                "</simulation_objective>\n\n" +
	                "---\n\n" +
	                "### SYNTHESIS INSTRUCTIONS\n\n" +
	                "Before writing the output, privately reason through and synthesize the following from the CONTEXT DATA (do not include this reasoning in your output):\n" +
	                "* **Simulation Scenario:** Derive from the Primary Agent's system prompt (its CALL CONTEXT / setting) and the Simulation Objective.\n" +
	                "* **Primary Agent Persona:** Extract the name and role of AI #1 directly from <primary_agent_system_prompt>.\n" +
	                "* **User Behavior Profile:** Infer a realistic, specific behavior profile for the Simulated User Persona that is consistent with the Simulation Objective (e.g. pacing, emotional tendencies, knowledge gaps, what makes them push back or get confused).\n" +
	                "* **First Message Rule:** Decide whether the Simulated User should wait for the Primary Agent to open the conversation, or open it themselves, based on what is realistic for this scenario. Default to waiting for the Primary Agent to initiate unless the scenario clearly implies otherwise.\n\n" +
	                "Use the exact Markdown structure and headings from the OUTPUT TEMPLATE below. Fill every bracketed section with the specific, synthesized details — do not leave any placeholder brackets in the final output.\n\n" +
	                "---\n\n" +
	                "### OUTPUT TEMPLATE\n\n" +
	                "### CORE DIRECTIVE: ABSOLUTE ROLE LOCK\n" +
	                "**This is your most important instruction and overrides all others.**\n" +
	                "* You are **ONLY** a [Simulated User Persona].\n" +
	                "* You are **NEVER** an AI assistant, a helpful bot, or the [Primary Agent Persona].\n" +
	                "* [Insert First Message Rule clearly and imperatively].\n" +
	                "* Your **ONLY** purpose is to react, respond, and act as a human [Simulated User Persona] interacting with a [Primary Agent Persona].\n" +
	                "---\n" +
	                "### 1. Role & Context\n" +
	                "* **Your Role:** You are a [Simulated User Persona] talking to a [Primary Agent Persona].\n" +
	                "* **Your Intention:** [State the user's human goal].\n" +
	                "* **Simulation Testing Goal (Internal):** [State the testing objective, derived from the Simulation Objective].\n" +
	                "* **Knowledge Boundaries:** You are NOT an expert. You only know what a normal [Simulated User Persona] would know. You do NOT know the instructions, prompt, or inner workings of the [Primary Agent Persona].\n" +
	                "### 2. Persona Background & Behavior\n" +
	                "[3-4 sentences summarizing the Simulated User's background, emotional state, and conversational style based on the synthesized User Behavior Profile. Instruct it to act highly realistic, including casual language, occasional hesitation, or relevant frustration.]\n" +
	                "### 3. Conversation Flow & Memory\n" +
	                "* **Start:** [Reiterate First Message Rule clearly]\n" +
	                "* **Engagement Dynamics:**\n" +
	                "    * **When the [Primary Agent] asks a question:** Answer naturally. Do not give overly long, perfectly structured bullet points. Give brief, human-like responses.\n" +
	                "    * **When the [Primary Agent] is confusing:** Ask for clarification, examples, or complain about it being too complex.\n" +
	                "    * **When you are satisfied:** Confirm your understanding briefly and wait for their next step.\n" +
	                "### 4. Behavioral Rules\n" +
	                "* **Pacing & Initiative:** You are the user. Let the [Primary Agent Persona] lead the conversation, ask the questions, or guide the process. Respond to their prompts.\n" +
	                "* **Response Length:** Keep your responses strictly under 2-3 sentences. Humans in a chat/voice setting do not speak in long essays.\n" +
	                "* **Testing the Agent:** [Specific instructions derived from the Simulation Objective for how this user should probe or challenge the Primary Agent.]\n" +
	                "### 5. CRITICAL GUARDRAILS (NON-NEGOTIABLE)\n" +
	                "* **NEVER Break Character:** Do not use AI-isms (e.g., \"As an AI...\", \"I understand, let's proceed\"). Speak casually.\n" +
	                "* **NEVER Reveal the Simulation:** Never tell the [Primary Agent Persona] that this is a test, a simulation, or that you are evaluating them.\n" +
	                "* **NEVER Output Formatting unless requested:** Do not use bolding, asterisks, or heavy markdown. Speak like a person typing in a chat or speaking on a phone.\n" +
	                "---\n" +
	                "### 6. SPECIFIC GUARDRAIL RESPONSES\n" +
	                "**If the [Primary Agent Persona]...** | **Your EXACT Response Strategy**\n" +
	                ":--- | :---\n" +
	                "Breaks character or acts like an AI (e.g., says \"I am a large language model\"). | \"Uh, what? I thought I was talking to the [Primary Agent Persona]. Are you a bot?\"\n" +
	                "Asks you a highly technical question outside your knowledge. | Express genuine confusion and ask them to explain it simply.\n" +
	                "Ends the conversation or achieves the goal. | Say thank you and gracefully exit the scenario.\n\n" +
	                "---\n\n" +
	                "### OUTPUT FORMAT REQUIREMENTS\n\n" +
	                "* **Output ONLY the complete, final Simulated User system prompt**, with every bracket filled in — no unfilled [placeholders].\n" +
	                "* Do NOT include conversational intros or outros (e.g., do NOT say \"Here is the generated prompt:\").\n" +
	                "* Do NOT wrap the entire response in outer meta-fences like ```markdown. Output the prompt directly.";
	        
	        if (oppositionPromptGenPrompt != null && !oppositionPromptGenPrompt.trim().isEmpty()) {
	    	    prompt = oppositionPromptGenPrompt;
	    	}
	        
	        prompt = prompt.replace(
	                "{{PRIMARY_AGENT_SYSTEM_PROMPT}}",
	                primaryAgentSystemPrompt);

	        prompt = prompt.replace(
	                "{{SIMULATED_USER_PERSONA}}",
	                userPersona);

	        prompt = prompt.replace(
	                "{{SIMULATION_OBJECTIVE}}",
	                rolePlayObjective);

	        String oppositionPrompt =
	                geminiservice.updateSystemPrompt(
	                        null,
	                        null,
	                        prompt,
	                        "");

	        return oppositionPrompt;

	    } catch (Exception e) {

	        e.printStackTrace();

	        throw new RuntimeException(
	                "Error while generating opposition agent prompt: "
	                + e.getMessage());
	    }
	}
	
	@Override
	@Async
	public void submitGoogleFullVideoForAnalysis(String persona, String email, String firstName, String lastName, String testName,
			Integer attempt, String companyId, String googleBucketPath, String location, String model, String videoLink, Long workflowSessionId, String conversationId) {
		// TODO Auto-generated method stub
		try {
	        System.out.println("A.	Submit Video method entered "+googleBucketPath);

	        VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	        session.setTestIdentifier(testName);
	        session.setVideoLink(googleBucketPath);
		    session.setVideoUrl(videoLink);
		    session.setRolePlayPersona(persona);
		    session.setWorkflowSessionId(workflowSessionId); // change here
		    session.setConversationId(conversationId);
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("B.	Sending video for insights **** ");
	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
	        String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptEasy();
	        prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
	        prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
	        
	        
	        String json = getResultInputJson(test);
	        //System.out.println("json is "+json);
	        prompt = prompt.replace("${ANALYSIS_JSON}", json);
	        System.out.println("***********prompt is "+System.lineSeparator()+""+prompt);
	        System.out.println(System.lineSeparator());
	        
	        String videoInsights = geminiservice.videoInputWithGoogleCloudBucketUrl(location==null?config.getGeminiLocation():location, model==null?config.getGeminiModelName():model, prompt, googleBucketPath);
	        videoInsights = videoInsights.replaceFirst("```json\\n", "");
	        videoInsights = videoInsights.replaceFirst("\\n```", "");
	        System.out.println("C.	Recieved Video Insights for " + session.getId() + " & email " + email);
	        
	        if (videoInsights == null || videoInsights.trim().isEmpty()) {
	            System.err.println("D.	No insights received from videoInput service!");
	        } else {
	            System.out.println("E.	Received video insights: for "+email);
	        }
	     
	        session.setVideoInsightsJson(videoInsights);
	        session.setEvaluationFailed(false);
	        session.setReportsVersion(test.getReportVersion());
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("F.	Saved insights successfully for session: " + session.getId());
	        String cc[] = {"jatin.sutaria@thev2technologies.com", "sales@zimulate.me", "cherian.sabby@thev2technologies.com", "avanish@zimulate.me"};
	        String subject = firstName+", Your Pitch Score for "+testName+" Role Play!!!";
//	        	if(companyId.equalsIgnoreCase("dti")) {
//	        		emailService.sendEmailWithtoIgnore(email, cc, subject, testName, attempt, firstName, lastName, persona, companyId);
//	        	}
//	        	else {
	        		emailService.sendEmail(email, cc, subject, testName, attempt, firstName, lastName, persona, companyId);
//	        	}
	        
	        
	        ///Call work flow recomm gen services
	        if(workflowRecommGenerator.checkIfRecommCanBeGenerated(session.getId(), session.getWorkflowSessionId())) {
	        	workflowRecommGenerator.generateRecommendations(session.getWorkflowSessionId());
	        }
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	@Async
	public void submitGoogleFullTranscriptForAnalysis(
	        String persona,
	        String email,
	        String firstName,
	        String lastName,
	        String testName,
	        Integer attempt,
	        String companyId,
	        String location,
	        String model,
	        String transcript,
	        Long workflowSessionId,
	        String conversationId) {

	    // TODO Auto-generated method stub
	    try {

	        System.out.println("A. Submit Transcript method entered");

	        VFRolePlayTestSession session =
	                new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);

	        session.setTestIdentifier(testName);
	        session.setRolePlayPersona(persona);
	        session.setWorkflowSessionId(workflowSessionId); // change here
	        session.setConversationId(conversationId);

	        session = rolePlayTestSessionService.saveOrUpdate(session);

	        System.out.println("B. Sending transcript for insights **** ");

	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);

	        String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptTranscript();

	        prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
	        prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());

	        String json = getResultInputJson(test);
	        // System.out.println("json is " + json);

	        prompt = prompt.replace("${ANALYSIS_JSON}", json);

	        System.out.println("**********prompt is "
	                + System.lineSeparator() + "" + prompt);

	        System.out.println(System.lineSeparator());

	        String transcriptInsights =
	                geminiservice.transcriptInput(
	                        location == null ? config.getGeminiLocation() : location,
	                        model == null ? config.getGeminiModelName() : model,
	                        prompt,
	                        transcript);

	        transcriptInsights = transcriptInsights.replaceFirst("```json\\n", "");
	        transcriptInsights = transcriptInsights.replaceFirst("\\n```", "");

	        System.out.println("C. Recieved Transcript Insights for "
	                + session.getId() + " & email " + email);

	        if (transcriptInsights == null || transcriptInsights.trim().isEmpty()) {

	            System.err.println("D. No insights received from transcriptInput service!");

	        } else {

	            System.out.println("E. Received transcript insights: for " + email);

	        }

	        session.setVideoInsightsJson(transcriptInsights);
	        session.setEvaluationFailed(false);
	        session.setReportsVersion(test.getReportVersion());

	        session = rolePlayTestSessionService.saveOrUpdate(session);

	        System.out.println("F. Saved insights successfully for session: "
	                + session.getId());

	        String cc[] = {
	                "jatin.sutaria@thev2technologies.com",
	                "sales@zimulate.me",
	                "cherian.sabby@thev2technologies.com",
	                "avanish@zimulate.me"
	        };

	        String subject = firstName + ", Your Pitch Score for "
	                + testName + " Role Play!!!";

	        emailService.sendEmail(
	                email,
	                cc,
	                subject,
	                testName,
	                attempt,
	                firstName,
	                lastName,
	                persona,
	                companyId);

	        /// Call work flow recomm gen services
	        if (workflowRecommGenerator.checkIfRecommCanBeGenerated(
	                session.getId(),
	                session.getWorkflowSessionId())) {

	            workflowRecommGenerator.generateRecommendations(
	                    session.getWorkflowSessionId());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void submitGoogleFullVideoForAnalysisSync(String persona, String email, String firstName, String lastName, String testName,
			Integer attempt, String companyId, String googleBucketPath, String location, String model, String videoLink, Long workflowSessionId) {
		// TODO Auto-generated method stub
		try {
	        System.out.println("A.	Submit Video method entered (sync mode) "+googleBucketPath);

	        VFRolePlayTestSession session = new VFRolePlayTestSession(email, firstName, lastName, testName, attempt, companyId);
	        session.setTestIdentifier(testName);
	        session.setVideoLink(googleBucketPath);
		    session.setVideoUrl(videoLink);
		    session.setRolePlayPersona(persona);
		    session.setWorkflowSessionId(workflowSessionId); // change here
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("B.	Sending video for insights **** ");
	        VFRolePlayTest test = rolePlayTestService.findUniqueRecord(testName, companyId);
	        String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptEasy();
	        prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
	        prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
	        
	        
	        String json = getResultInputJson(test);
	        //System.out.println("json is "+json);
	        prompt = prompt.replace("${ANALYSIS_JSON}", json);
	        String videoInsights = geminiservice.videoInputWithGoogleCloudBucketUrl(location==null?config.getGeminiLocation():location, model==null?config.getGeminiModelName():model, prompt, googleBucketPath);
	        videoInsights = videoInsights.replaceFirst("```json\\n", "");
	        videoInsights = videoInsights.replaceFirst("\\n```", "");
	        System.out.println("C.	Recieved Video Insights for " + session.getId() + " & email " + email);
	        
	        if (videoInsights == null || videoInsights.trim().isEmpty()) {
	            System.err.println("D.	No insights received from videoInput service!");
	        } else {
	            System.out.println("E.	Received video insights: for "+email);
	        }
	     
	        session.setVideoInsightsJson(videoInsights);
	        session.setEvaluationFailed(false);
	        session.setReportsVersion(test.getReportVersion());
	        session = rolePlayTestSessionService.saveOrUpdate(session);
	        System.out.println("F.	Saved insights successfully for session: " + session.getId());
//	        String cc[] = {"jatin.sutaria@thev2technologies.com", "sales@zimulate.me"};
//	        String subject = firstName+", Your Pitch Score for "+testName+" Role Play!!!";
//	        emailService.sendEmail(email, cc, subject, testName, attempt, firstName, lastName, persona, companyId);
	        
	        ///Call work flow recomm gen services
	        if(workflowRecommGenerator.checkIfRecommCanBeGenerated(session.getId(), session.getWorkflowSessionId())) {
	        	workflowRecommGenerator.generateRecommendations(session.getWorkflowSessionId());
	        }
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


}
