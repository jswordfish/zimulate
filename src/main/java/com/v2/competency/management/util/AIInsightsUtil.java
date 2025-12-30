package com.v2.competency.management.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.dto.RelevancyScoreForAnswerDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.entities.Question;

public class AIInsightsUtil {
	
	static ObjectMapper mapper = new ObjectMapper();
	
	private static String RELEVANCY_PROMPT = "In a verbal assessment for a test taker, the question asked is below in single quotes. ${NEW_LINE}\n"
			+ "\n"
			+ "'${QUESTION}' ${NEW_LINE}\n"
			+ "The answer is an audio file shared.\n"
			+ "\n"
			+ "Can you analyse the relevancy of the answer with respect to the Question asked and share your relevancy observations & a score in percentage. If the answer is too short score it as 0%. ${NEW_LINE}\n"
			+ "\n"
			+ "Response of your Analysis should strictly be a json file based on instructions below.${NEW_LINE}\n"
			+ " \n"
			+ "Post your analysis can you return the results json in following format BELOW?${NEW_LINE}\n"
			+ "${RELEVANCY_JSON}";
	
	
	/**
	 * transctipt includes Q/A plus the prompt plus the expected json format
	 * @param q
	 * @param userAnswer
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String generateTranscriptBasedOnCustomPromptInQ(Question q, String userAnswer) throws JsonProcessingException {
		String prompt = q.getAiInsightsPrompt() ;
		String transcipt = prompt.replace("${NEW_LINE}", System.lineSeparator());
		String questionText = q.getQuestionText();
		String qa = questionText +System.lineSeparator()+userAnswer;
		transcipt = transcipt.replace("${TRANSCRIPT}", qa);
		
		String mult = q.getMultipleCompetenciesAssociatedWithQuestion();
		
		String diffParents[] = mult.split("###");
		List<String> list = new ArrayList<>();
		for(String diffParent : diffParents) {
			String comb[] = StringUtils.split(diffParent, "$$$");
			String parent = comb[0];
			String childs[] = comb[1].split("__");
			for(String c : childs) {
				c = c.trim();
				list.add(parent+"--"+c);
			}
			
		}
		
		RolePlayInsightsDto dto = new RolePlayInsightsDto();
		for(String param : list) {
			RoleplayInsightsDetail det = new RoleplayInsightsDetail();
			
			dto.getMapCompetenciesInsights().put(param, det);
		}
	String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
	transcipt = transcipt.replace("${ANALYSIS_JSON}", json);
	return transcipt;
		
	}
	
	public static String generateAudioBasedTranscriptBasedOnCustomPromptInQ(Question q) throws JsonProcessingException {
		String prompt = q.getAiInsightsPrompt() ;
		String transcipt = prompt.replace("${NEW_LINE}", System.lineSeparator());
		String questionText = q.getQuestionText();
		String qa = questionText +System.lineSeparator();
		transcipt = transcipt.replace("${TRANSCRIPT}", qa);
		
		String mult = q.getMultipleCompetenciesAssociatedWithQuestion();
		
		String diffParents[] = mult.split("###");
		List<String> list = new ArrayList<>();
		for(String diffParent : diffParents) {
			String comb[] = StringUtils.split(diffParent, "$$$");
			String parent = comb[0];
			String childs[] = comb[1].split("__");
			for(String c : childs) {
				c = c.trim();
				list.add(parent+"--"+c);
			}
			
		}
		
		RolePlayInsightsDto dto = new RolePlayInsightsDto();
		for(String param : list) {
			RoleplayInsightsDetail det = new RoleplayInsightsDetail();
			
			dto.getMapCompetenciesInsights().put(param, det);
		}
	String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
	transcipt = transcipt.replace("${ANALYSIS_JSON}", json);
	return transcipt;
		
	}
	
	public static String generateAudioBasedRelevancyPompt(Question q) throws JsonProcessingException {
		String prompt = RELEVANCY_PROMPT;
		String transcipt = prompt.replace("${NEW_LINE}", System.lineSeparator());
		transcipt = transcipt.replace("${QUESTION}", q.getQuestionText());
	String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new RelevancyScoreForAnswerDto());
	transcipt = transcipt.replace("${RELEVANCY_JSON}", json);
	return transcipt;
		
	}

}
