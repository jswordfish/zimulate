package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.MCQScoreForUserDto;
import com.v2.competency.management.dtos.QuestionAvailabilityCountDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public interface VFTestUserQuestionAnswerRepo extends CrudRepository<VFTestUserQuestionAnswer, Long>{
	
	@Query("select v from VFTestUserQuestionAnswer v where testIdentifier =:testIdentifier  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt and v.qid =:qid")
	public VFTestUserQuestionAnswer findUniqueQuestionAnswerByEmail(@Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt, @Param("qid")  String qid);
	
	@Query("select v from VFTestUserQuestionAnswer v where testIdentifier =:testIdentifier  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt order by v.id")
	public List<VFTestUserQuestionAnswer> findAllQAForUser(@Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	

	@Query("select v.question from VFTestUserQuestionAnswer v where testIdentifier =:testIdentifier  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt order by v.id")
	public List<String> findAllQuesTextForUser(@Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	
	@Query("select v from VFTestUserQuestionAnswer v where testIdentifier =:testIdentifier  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt and v.questionType = 'MCQ' and v.competency =:competency and v.parentCompetency =:parentCompetency   order by v.id")
	public List<VFTestUserQuestionAnswer> findMCQQAForUserByCompetency(@Param("parentCompetency") String parentCompetency, @Param("competency") String competency, @Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	@Query("select v from VFTestUserQuestionAnswer v where testIdentifier =:testIdentifier  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt and v.questionType = 'SUBJECTIVE' and v.competency =:competency and v.parentCompetency =:parentCompetency   order by v.id")
	public List<VFTestUserQuestionAnswer> findSubjectiveQAForUserByCompetency(@Param("parentCompetency") String parentCompetency, @Param("competency") String competency, @Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	
	@Query("select v from VFTestUserQuestionAnswer v where v.testIdentifier =:testIdentifier and v.companyId =:companyId and v.questionType = 'MCQ' group by v.questionType")
	public VFTestUserQuestionAnswer findMCQPresenceForTest( @Param("testIdentifier") String testIdentifier,  @Param("companyId") String companyId);
	
	@Query("select v from VFTestUserQuestionAnswer v where v.testIdentifier =:testIdentifier and v.companyId =:companyId and v.questionType = 'Subjective' and v.aiInsightsGenerated=true group by v.question")
	public List<VFTestUserQuestionAnswer> findDistincyScenarioQsForTest(@Param("testIdentifier") String testIdentifier,  @Param("companyId") String companyId);
	
	
	@Query("select v from VFTestUserQuestionAnswer v where v.question = :question and v.testIdentifier =:testIdentifier and v.companyId =:companyId and v.questionType = 'Subjective' ")
	public List<VFTestUserQuestionAnswer> findScenarioAnswersForTestQuestion(@Param("question") String question, @Param("testIdentifier") String testIdentifier,  @Param("companyId") String companyId);
	
	/**
	 * Dynamic reporting data quesries below
	 */
	//@Query("select new com.v2.competency.management.dtos.MCQScoreForUserDto(v.email, v.attempt, v.firstName, v.lastName,  AVG(v.scoreInPercent), count(v)) from VFTestUserQuestionAnswer v where  v.testIdentifier  =:testIdentifier and v.questionType = 'MCQ' and v.email =: email and v.companyId =:companyId group by  v.questionType, v.attempt")
	//List<MCQScoreForUserDto> findMCQScoreForUserByAssessment(@Param("testIdentifier") String testIdentifier, @Param("email") String email, @Param("companyId") String companyId);
	
	
	@Query("select new com.v2.competency.management.dtos.MCQScoreForUserDto(v.email, v.firstName, v.lastName, v.testIdentifier,  AVG(v.scoreInPercent), count(v),  v.attempt) from VFTestUserQuestionAnswer v where  v.testIdentifier  =:testIdentifier and v.questionType = 'MCQ' and v.companyId =:companyId group by  v.email, v.attempt")
	List<MCQScoreForUserDto> findMCQScoreForUserByAssessment2(@Param("testIdentifier") String testIdentifier, @Param("companyId") String companyId);
	
	//public MCQScoreForUserDto(String email, String firstName, String lastName, String testIdentifier, Double averageScore, Long count, Long attempt)
	
	@Query("select v from VFTestUserQuestionAnswer v where v.testIdentifier =:testIdentifier and v.companyId =:companyId and v.questionType = 'Subjective' and v.aiInsightsGenerated=true")
	public List<VFTestUserQuestionAnswer> findAllSubjectiveAnswersForTest(@Param("testIdentifier") String testIdentifier,  @Param("companyId") String companyId);
}


