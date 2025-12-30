package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.RolePlayQuestionAnswer;


public interface RolePlayQuestionAnswerService {
	
	public List<RolePlayQuestionAnswer> findAllQAForUser( String testName, String email, String companyId, Integer attempt);
	
	public List<String> findAllQuesTextForUser(String testName, String email, String companyId, Integer attempt);
	
	public List<String> findAllAnsTextForUser(String testName, String email, String companyId, Integer attempt);

}
