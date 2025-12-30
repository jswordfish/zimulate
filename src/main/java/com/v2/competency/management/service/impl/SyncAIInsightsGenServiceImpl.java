package com.v2.competency.management.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.dto.RelevancyScoreForAnswerDto;
import com.googlecloud.vertex.ai.insights.dto.EnergyGraphDto;
import com.googlecloud.vertex.ai.insights.dto.EnergyLevelDetail;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.repos.VFTestUserQuestionAnswerRepo;
import com.v2.competency.management.service.AIResponseGeneratorService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.RelevancyCheckerService;
import com.v2.competency.management.service.SyncAIInsightsGenService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import com.v2.competency.management.util.AIInsightsUtil;

import lombok.experimental.UtilityClass;

@Service
@Transactional
public class SyncAIInsightsGenServiceImpl implements SyncAIInsightsGenService{
	
	@Autowired
	VFTestUserQuestionAnswerRepo questionAnswerRepo;
	
	@Autowired
	AIResponseGeneratorService aiResponseGeneratorService;
	
	@Autowired
	UserCompetencyWiseScoreForAssessmentService userCompetencyWiseScoreForAssessmentService;
	
	@Autowired
	VFRolePlayTestSessionService rolePlayTestSessionService;
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
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
	           "\n6. **Grammar**: Identify any grammatical errors or mispronunciations."  
	           +System.lineSeparator()
		       	+"Post your analysis can you return the results json in following format BELOW?"
		       	+"${ANALYSIS_JSON}"+System.lineSeparator()+
		       	"\n And strictly return only the json structure and no extra words or characters like ```json``` do not add this in the start or end of the response"+
		        "\n If the video does not contain any relevant speech (e.g., no speaker present, background noise only, or silent footage), return the JSON structure with a score of 0 for all parameters.";
	
	String energyAnalysisPrompt = "Analyze the energy level of the speaker in the provided video. "
	        + "Divide the analysis into time-based segments and return the energy levels as percentages. "
	        + "Output the result in the following JSON format:\n"
	        + "{\n"
	        + "  \"timeBasedElements\" : [\n"
	        + "    { \"timeRange\" : \"0-1 min\", \"percentEnergyLevel\" : 50 },\n"
	        + "    { \"timeRange\" : \"1-2 min\", \"percentEnergyLevel\" : 60 },\n"
	        + "    { \"timeRange\" : \"2-3 min\", \"percentEnergyLevel\" : 30 },\n"
	        + "    { \"timeRange\" : \"3-4 min\", \"percentEnergyLevel\" : 80 }\n"
	        + "  ]\n"
	        + "}\n\n"
	        + "Use the following structure for analysis:\n"
	        + "${ANALYSIS_JSON}";
			
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	RelevancyCheckerService relevancyCheckerService;
	
	@Autowired
	QuestionService  questionService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	GeminiAudioVideoService audioVideoService;
	
	@PostConstruct
    public void init() throws JsonProcessingException {
    	
    	mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

	@Override
	public List<UserCompetencyWiseScoreForAssessment> generateInsightsForScenarioBasedQsInSync(String testName,
			Map<String, List<VFTestUserQuestionAnswer>> mapSubjectiveQs, String testIdentifier, String email,
			String companyId, Integer attempt) {
		// TODO Auto-generated method stub
				System.out.println("In SyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync start "+mapSubjectiveQs.size()+"  "+ mapSubjectiveQs );
				List<UserCompetencyWiseScoreForAssessment> ret = new ArrayList<>();
				
				for(String key : mapSubjectiveQs.keySet()) {
					String comps[] = key.split("###");
					String parentComp = comps[0];
					String comp = comps[1];
					UserCompetencyWiseScoreForAssessment userCompetencyWiseScoreForAssessment = userCompetencyWiseScoreForAssessmentService.findUniqueRecord(email, testName, testIdentifier, comp, parentComp, attempt, Question_Type.SUBJECTIVE.getType(), companyId);
					System.out.println("In SyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync 1. "+userCompetencyWiseScoreForAssessment );
					
					
					
					if((userCompetencyWiseScoreForAssessment == null) || (userCompetencyWiseScoreForAssessment.getEvaluationFailed() != null && userCompetencyWiseScoreForAssessment.getEvaluationFailed()) )  {
						List<VFTestUserQuestionAnswer> list = mapSubjectiveQs.get(key);
						Float totalCompetencyWiseScore = 0f;
						Boolean evaluationFailed = false;
						String ids = "";
						
						for(VFTestUserQuestionAnswer ans : list) {
							ids += ans.getId()+",";
						}
						for(VFTestUserQuestionAnswer ans : list) {
							Boolean correct = true;
							if(ans.getQuestionType().equalsIgnoreCase(Question_Type.SUBJECTIVE.getType())) {
								System.out.println("In SyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync 2. " );
								
								try {
									VFTestUserQuestionAnswer answer2 = questionAnswerRepo.findById(ans.getId()).get();
									System.out.println("In SyncAIInsightsGenServiceImpl.generateInsightsForScenarioBasedQsInAsync 2.5. "+answer2.getAiInsightsGenerated() );
									if(!answer2.getAiInsightsGenerated()) {
										
										
										RelevancyScoreForAnswerDto relevancy =  null;
										InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = null;
										RolePlayInsightsDto customAISightsForScenarioBasedQuestion = null;
										
										Question q = questionService.findById(Long.parseLong( answer2.getQid()));
										if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && answer2.getAnswerAudioOrVideo() != null) {
//											String relPromp = AIInsightsUtil.generateAudioBasedRelevancyPompt(q);
//											String fileName = ans.getAnswerAudioOrVideo().substring(ans.getAnswerAudioOrVideo().lastIndexOf("/")+1, ans.getAnswerAudioOrVideo().length());
//											String baseLoc = config.getFileServerPath();
//											String loc = "";
//											 loc = baseLoc + java.io.File.separator + ans.getCompanyId() +java.io.File.separator + ans.getTestIdentifier() +File.separator+ans.getEmail()+File.separator+ans.getAttempt()+File.separator+ans.getQid();
//											
//											File file = new File(loc+File.separator+fileName);
//											String relevancyJson = audioVideoService.processAudioFile(relPromp, file);
//											relevancy = mapper.readValue(relevancyJson.getBytes(), RelevancyScoreForAnswerDto.class);
											//relevancy = new RelevancyScoreForAnswerDto();
											relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
										}
										else {
											relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
										}
										
											if(relevancy.getRelevanceOfAnswerScoreInPercent() > 10) {
												//String qid = answer2.getQid();
											//	Question q =  questionService.findById(Long.parseLong(qid));
												if(q.getAiInsightsPrompt() != null && q.getAiInsightsPrompt().trim().length() > 0) {
													String transcriptWithExpectedJsonFormat = AIInsightsUtil.generateTranscriptBasedOnCustomPromptInQ(q, answer2.getAnswer());
													String customAISightsForScenarioBasedQuestionStringJson = null;
													if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && answer2.getAnswerAudioOrVideo() != null) {
														System.out.println("analysing audio response 1");
														String fileName = answer2.getAnswerAudioOrVideo().substring(answer2.getAnswerAudioOrVideo().lastIndexOf("/")+1, answer2.getAnswerAudioOrVideo().length());
														String baseLoc = config.getFileServerPath();
														String loc = "";
														 loc = baseLoc + java.io.File.separator + answer2.getCompanyId() +java.io.File.separator + answer2.getTestIdentifier() +File.separator+answer2.getEmail()+File.separator+answer2.getAttempt()+File.separator+answer2.getQid();
														
														File file = new File(loc+File.separator+fileName);
														System.out.println("analysing audio response audio file "+file.getAbsolutePath());
														String audioPrompt = q.getAiInsightsPrompt();
														audioPrompt = AIInsightsUtil.generateAudioBasedTranscriptBasedOnCustomPromptInQ(q);
														//System.out.println("analysing audio response prompt "+audioPrompt);
														customAISightsForScenarioBasedQuestionStringJson = audioVideoService.processAudioFile(audioPrompt, file);
														System.out.println("analysing audio response done ");
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
										
										
										
										if(insightForScenarioBasedQuestion != null) {
											totalCompetencyWiseScore += insightForScenarioBasedQuestion.getOverAllScoreInPErcentage();
										}
										else {
											totalCompetencyWiseScore += customAISightsForScenarioBasedQuestion.getOverAllScoreInPercent();
										}
										
									}
									else {
										totalCompetencyWiseScore += answer2.getOverallScoreIncaseOfSubjectiveByReviewer() == null?answer2.getOverallScoreIncaseOfSubjective():answer2.getOverallScoreIncaseOfSubjectiveByReviewer();
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
							if(userCompetencyWiseScoreForAssessment == null) {
								userCompetencyWiseScoreForAssessment = createUserCompetencyWiseScoreForAssessmentRecord(testName, testIdentifier, email, companyId, attempt, parentComp, comp, competencyWiseScore, false, ids);
							}
							else {
								userCompetencyWiseScoreForAssessment.setEvaluationFailed(evaluationFailed);
								userCompetencyWiseScoreForAssessment.setAverageScore(competencyWiseScore);
							}
							
						}
						else {
							if(userCompetencyWiseScoreForAssessment == null) {
							userCompetencyWiseScoreForAssessment = createUserCompetencyWiseScoreForAssessmentRecord(testName, testIdentifier, email, companyId, attempt, parentComp, comp, null, true, ids);
							}
							else {
								userCompetencyWiseScoreForAssessment.setEvaluationFailed(evaluationFailed);
								//userCompetencyWiseScoreForAssessment.setAverageScore(competencyWiseScore);
							}
						}
						ret.add(userCompetencyWiseScoreForAssessment);
					}
					else {
						ret.add(userCompetencyWiseScoreForAssessment);
					}
					
					
					
					
				}
				return ret;
	}
	
	
	private UserCompetencyWiseScoreForAssessment createUserCompetencyWiseScoreForAssessmentRecord(String testName, String testIdentifier, String email, String companyId, Integer attempt, String parentComp, String comp, Float competencyWiseScore, Boolean evaluationFailed, String ansIds) {
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
		return competencyWiseScoreForAssessment;
	}
	
	@Override
	@Async
	public void generateInsightsForRolePlayBasedAssessment(String testName, String email, String companyId,
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
			input = input.replace("`", "");
			input = input.replace('\u00A0',' ');
			input = input.replace("'", " ");
			String insights =  aiResponseGeneratorService.generateRolePlayAnalysisForRolePlayTest(input);
			System.out.println("insights json \n"+insights);
			System.out.println("------");
			RolePlayInsightsDto res = mapper.readValue(insights.getBytes(), RolePlayInsightsDto.class);
			rolePlayTestSession.setInsightsJson(insights);
			rolePlayTestSession.setEvaluationFailed(false);
			rolePlayTestSession.setFinalScore(res.getOverAllScoreInPercent()*1.0f);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rolePlayTestSession.setEvaluationFailed(true);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void generateInsightsForRolePlayBasedAssessmentSync(String testName, String email, String companyId,
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
			input = input.replace("`", "");
			input = input.replace('\u00A0',' ');
			input = input.replace("'", " ");
			String insights =  aiResponseGeneratorService.generateRolePlayAnalysisForRolePlayTest(input);
			System.out.println("insights json \n"+insights);
			System.out.println("------");
			RolePlayInsightsDto res = mapper.readValue(insights.getBytes(), RolePlayInsightsDto.class);
			rolePlayTestSession.setInsightsJson(insights);
			rolePlayTestSession.setEvaluationFailed(false);
			rolePlayTestSession.setFinalScore(res.getOverAllScoreInPercent()*1.0f);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rolePlayTestSession.setEvaluationFailed(true);
			rolePlayTestSessionService.saveOrUpdate(rolePlayTestSession);
			throw new RuntimeException(e);
		}
	}


	@Override
	public void generateInsightsForScenarioBasedAnswer(Long answerId) {
		try {
			VFTestUserQuestionAnswer answer2 = questionAnswerRepo.findById(answerId).get();
			RelevancyScoreForAnswerDto relevancy =  null;
			InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = null;
			RolePlayInsightsDto customAISightsForScenarioBasedQuestion = null;
			
			Question q = questionService.findById(Long.parseLong( answer2.getQid()));
			if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && answer2.getAnswerAudioOrVideo() != null) {
//				String relPromp = AIInsightsUtil.generateAudioBasedRelevancyPompt(q);
//				String fileName = answer2.getAnswerAudioOrVideo().substring(answer2.getAnswerAudioOrVideo().lastIndexOf("/")+1, answer2.getAnswerAudioOrVideo().length());
//				String baseLoc = config.getFileServerPath();
//				String loc = "";
//				 loc = baseLoc + java.io.File.separator + answer2.getCompanyId() +java.io.File.separator + answer2.getTestIdentifier() +File.separator+answer2.getEmail()+File.separator+answer2.getAttempt()+File.separator+answer2.getQid();
//				
//				File file = new File(loc+File.separator+fileName);
//				String relevancyJson = audioVideoService.processAudioFile(relPromp, file);
//				relevancy = mapper.readValue(relevancyJson.getBytes(), RelevancyScoreForAnswerDto.class);
				//relevancy = new RelevancyScoreForAnswerDto();
				relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
			}
			else {
				relevancy =  relevancyCheckerService.checkIfAnswerRelevant(answer2);
			}
			
				if(relevancy.getRelevanceOfAnswerScoreInPercent() > 10) {
					//String qid = answer2.getQid();
				//	Question q =  questionService.findById(Long.parseLong(qid));
					if(q.getAiInsightsPrompt() != null && q.getAiInsightsPrompt().trim().length() > 0) {
						String transcriptWithExpectedJsonFormat = AIInsightsUtil.generateTranscriptBasedOnCustomPromptInQ(q, answer2.getAnswer());
						String customAISightsForScenarioBasedQuestionStringJson = null;
						if(q.getAnalyzeSound() != null && q.getAnalyzeSound() && answer2.getAnswerAudioOrVideo() != null) {
							System.out.println("analysing audio response 1");
							String fileName = answer2.getAnswerAudioOrVideo().substring(answer2.getAnswerAudioOrVideo().lastIndexOf("/")+1, answer2.getAnswerAudioOrVideo().length());
							String baseLoc = config.getFileServerPath();
							String loc = "";
							 loc = baseLoc + java.io.File.separator + answer2.getCompanyId() +java.io.File.separator + answer2.getTestIdentifier() +File.separator+answer2.getEmail()+File.separator+answer2.getAttempt()+File.separator+answer2.getQid();
							
							File file = new File(loc+File.separator+fileName);
							System.out.println("analysing audio response audio file "+file.getAbsolutePath());
							String audioPrompt = q.getAiInsightsPrompt();
							audioPrompt = AIInsightsUtil.generateAudioBasedTranscriptBasedOnCustomPromptInQ(q);
							//System.out.println("analysing audio response prompt "+audioPrompt);
							customAISightsForScenarioBasedQuestionStringJson = audioVideoService.processAudioFile(audioPrompt, file);
							System.out.println("analysing audio response done ");
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	
	@Override
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
	            vidDto.getMapCompetenciesInsights().put(vidParam, vidDet);
	        }
	        
	        String vidJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vidDto);
	        String vidInput = rolePlayInsightsPromptWithVideo.replace("${ANALYSIS_JSON}", vidJson);
	        
	        System.out.println("Sending video for insights | Path: " + mergedVideoPath);
	        
	        String videoInsights = audioVideoService.videoInput(vidInput, mergedVideoPath);
	        
	        
	        
	        
//	        System.out.println(mergedVideoPath);

	        
	      //  res.setVideoInsights(videoInsights);
	        
	        System.out.println("Video Insights for " + rolePlayTestSession.getId() + ": " + videoInsights);
	        	        
	        
	        
//	        String[] timeRanges = {"0-1 min", "1-2 min", "2-3 min", "3-4 min"};
//
//	        EnergyGraphDto energyDto = new EnergyGraphDto();
//
//	        for (String timeRange : timeRanges) {
//	            EnergyLevelDetail detail = new EnergyLevelDetail(timeRange, 50); 
//	            energyDto.getTimeBasedElements().put(timeRange, detail);
//	        }
//
//	        ObjectMapper mapper = new ObjectMapper();
//	        String energyGraphJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(energyDto);
//
//	        String energyAnalysisPromptInput = energyAnalysisPrompt.replace("${ANALYSIS_JSON}", energyGraphJson);
//	        
//	        System.out.println("Sending video for energy graph");
//	        
//	        String videoEnergyJson = audioVideoService.videoInput(energyAnalysisPromptInput, mergedVideoPath);
//	        
//	        System.out.println("Energy graph : "+ videoEnergyJson);

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
	

}
