package com.v2.competency.management.common.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;

public class VFTestInterviewFlowUtil {
	
	static Map<Integer, Integer> total_qs_followup_qs_map = new HashMap<>();
	
	static {
		total_qs_followup_qs_map.put(4, 1);
		total_qs_followup_qs_map.put(6, 2);
		total_qs_followup_qs_map.put(8, 2);
		total_qs_followup_qs_map.put(10, 3);
		total_qs_followup_qs_map.put(10, 2);
		total_qs_followup_qs_map.put(12, 3);
		total_qs_followup_qs_map.put(13, 3);
	}
	
	
	
	public static CompetencyTest calculate(List<CompetencyQuestion> competencies) throws JsonProcessingException {
		
		Integer noOfQuestions = 0;
			if(competencies.size() == 1) {
				noOfQuestions = 4;
			}
			else if(competencies.size() == 2) {
				noOfQuestions = 6;
			}
			else if(competencies.size() == 3 || competencies.size() == 4) {
				noOfQuestions = 8;
			}
			else if(competencies.size() >= 5 && competencies.size() <= 7) {
				noOfQuestions = 10;
			}
			else if(competencies.size() >= 8 && competencies.size() <= 9) {
				noOfQuestions = 12;
			}
			else if(competencies.size() == 10) {
				noOfQuestions = 13;
			}
			
		Integer noFollowups = total_qs_followup_qs_map.get(noOfQuestions);
		Integer directQs = noOfQuestions - noFollowups;
		List<CompetencyQuestion> generatedQs = generateLevel1Qs(competencies, directQs);
		generatedQs = addRandomFollowups(generatedQs, noFollowups);
		CompetencyTest competencyTest = CompetencyTest.builder().competencies(generatedQs).build();

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
		return competencyTest;
	}
	
	private static List<CompetencyQuestion> generateLevel1Qs(List<CompetencyQuestion> competencies, Integer directQs){
		int count = 0;
		List<CompetencyQuestion> directQCompetencies = new ArrayList<>();
		int index = 0;
		for(int i=0;i<directQs;i++) {
				if(index >= (competencies.size())) {
					index = 0;
				}
			CompetencyQuestion q = competencies.get(index);
			index++;
			if(count < directQs) {
				CompetencyQuestion q1 = (CompetencyQuestion)SerializationUtils.clone(q);
				directQCompetencies.add(q1);
				count++;
			}
			else {
				break;
			}
		}
		
		return directQCompetencies;
	}
	
//	private static List<CompetencyQuestion> generateLevel1Qs(List<CompetencyQuestion> competencies, Integer directQs){
//		int count = 0;
//		List<CompetencyQuestion> directQCompetencies = new ArrayList<>();
//		for(int i=0;i<competencies.size();i++) {
//			CompetencyQuestion q = competencies.get(i);
//			if(count < directQs) {
//				CompetencyQuestion q1 = (CompetencyQuestion)SerializationUtils.clone(q);
//				directQCompetencies.add(q1);
//				count++;
//				if(i == (competencies.size() - 1)) {
//					i = 0;
//				}
//			}
//			else {
//				break;
//			}
//		}
//		
//		return directQCompetencies;
//	}
	
	private static List<CompetencyQuestion> addRandomFollowups(List<CompetencyQuestion> directQCompetencies, Integer noFollowups){
		directQCompetencies.sort(new Comparator<CompetencyQuestion>() {

			@Override
			public int compare(CompetencyQuestion q1, CompetencyQuestion q2) {
				// TODO Auto-generated method stub
				return (q1.getWeightOfQuestion() < q2.getWeightOfQuestion() ) ? 1 : ((q1.getWeightOfQuestion() == q2.getWeightOfQuestion()) ? 0 : -1);
			}
		});
		Integer count = 0;
		//for(CompetencyQuestion q : directQCompetencies) {
		for(int i=0;i<directQCompetencies.size();i++) {
			CompetencyQuestion q  = directQCompetencies.get(i);
			if(count < noFollowups) {
				CompetencyQuestion q1 = (CompetencyQuestion)SerializationUtils.clone(q);
				q.getFollowups().add(q1);
				//directQCompetencies.add(q1);
				q.setFollowup(true);
				count++;
					if(i == (directQCompetencies.size()-1)) {
						i = 0;
					}
			}
			else {
				break;
			}
		}
	return directQCompetencies;
	}
	


}
