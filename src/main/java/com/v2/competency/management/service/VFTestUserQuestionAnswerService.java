package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.MCQScoreForUserDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public interface VFTestUserQuestionAnswerService {
	
	
	//public VFTestUserQuestionAnswer findQuestionAnswerByEmail(String email, String companyId, String testIdentifier, Integer attempt, String question, String answer);
	
	
	public VFTestUserQuestionAnswer create(VFTestUserQuestionAnswer questionAnswer);
	
	public VFTestUserQuestionAnswer getById(Long id);
	
	
	public List<VFTestUserQuestionAnswer> findAllQAForUser( String testIdentifier, String email, String companyId, Integer attempt);
	
	public List<String> findAllQuesTextForUser(String testIdentifier, String email, String companyId, Integer attempt);
	

	public List<VFTestUserQuestionAnswer> findMCQQAForUserByCompetency(@Param("parentCompetency") String parentCompetency, @Param("competency") String competency, @Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	public List<VFTestUserQuestionAnswer> findSubjectiveQAForUserByCompetency(@Param("parentCompetency") String parentCompetency, @Param("competency") String competency, @Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	
	
	public VFTestUserQuestionAnswer findMCQPresenceForTest(String testIdentifier,  String companyId);
	
	public List<VFTestUserQuestionAnswer> findDistincyScenarioQsForTest( String testIdentifier, String companyId);
	
	
	public List<VFTestUserQuestionAnswer> findScenarioAnswersForTestQuestion( String question, String testIdentifier, String companyId);
	
	/**
	 * Dynamic reporting data quesries below
	 */
	List<MCQScoreForUserDto> findMCQScoreForUserByAssessment( String testIdentifier,  String companyId);
	
	public List<VFTestUserQuestionAnswer> findAllSubjectiveAnswersForTest(String testIdentifier,  String companyId);
	
}
