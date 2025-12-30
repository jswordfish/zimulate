package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.QuestionAvailabilityCountDto;
import com.v2.competency.management.entities.Question;

public interface QuestionService {
	
	 Question findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(String questionText, String competency, String parentCompetency, String companyId);
	 
	
	 
	 Page<Question> findAllQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 Page<Question> findPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 Page<Question> findUnPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 

	 Question saveOrUpdate(Question question);
	 
	 void deleteQuestion(Long id);
	 
	 void publishQuestion(Long id);
	 
	 void unPublishQuestion(Long id);
	 
	 List<Question> fetchMCQQuestions(String prompt, String competency, String parentCompetency);
	 
	 List<String> fetchTextBasedQuestions(String prompt, String competency, String parentCompetency);
	 
	 Page<Question> findPublishedMCQQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 Page<Question> findPublishedTextBasedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 Question findById(Long id);
	 
	 List<Question> findRandomQuestionsByCompetencyForCompanyWithType(String competency, String parentCompetency,  String questionType, String companyId , Integer numOfQs);
		
	 Page<Question> findPublishedQuestionsByCompetencyAndQuestionTypeForCompany(@Param("questionType")  String questionType, @Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 List<QuestionAvailabilityCountDto> findQuestionAvailabilityCount(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId);

	 List<Question> findAllQuestionsAssociatedWithMultipleCompetenciesByCompetencyForCompany(String questionType, @Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId);
	 
}
