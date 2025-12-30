package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.QuestionAvailabilityCountDto;
import com.v2.competency.management.entities.Question;

public interface QuestionRepo extends CrudRepository<Question, Long> {

	
	 public Question findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(String questionText, String competency, String parentCompetency, String companyId);
	 
	
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findAllQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and q.published = true and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and q.published = true and q.questionType=:questionType and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findPublishedQuestionsByCompetencyAndQuestionTypeForCompany(@Param("questionType")  String questionType, @Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and q.published = false and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findUnPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and q.published = true and q.questionType = 'MCQ' and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findPublishedMCQQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 @Query("select q from Question q where q.competency=:competency and q.parentCompetency=:parentCompetency and q.companyId=:companyId and q.published = true and q.questionType = 'SUBJECTIVE' and (q.softDelete is null or q.softDelete=false)")
	 Page<Question> findPublishedTextBasedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
	 
	 @Query(value="select q.* from question q where q.competency=:competency and q.parent_competency=:parentCompetency and q.question_type=:questionType and q.company_id=:companyId and (q.soft_delete is null or q.soft_delete=false) order by rand() limit :numOfQs", nativeQuery=true)
	 List<Question> findRandomQuestionsByCompetencyForCompanyWithType(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("questionType")  String questionType, @Param("companyId")  String companyId , @Param("numOfQs")  Integer numOfQs);
	
	 @Query("SELECT " +
	           "    new com.v2.competency.management.dtos.QuestionAvailabilityCountDto(q.competency, q.parentCompetency, q.questionType, COUNT(q)) " +
	           "FROM " +
	           "    Question q where ((q.competency=:competency and q.parentCompetency=:parentCompetency) OR (q.multipleCompetenciesAssociatedWithQuestion like :competencyLike and q.multipleCompetenciesAssociatedWithQuestion like :parentCompetencyLike)) and q.companyId=:companyId  GROUP BY q.questionType")
	List<QuestionAvailabilityCountDto> findQuestionAvailabilityCount(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,@Param("competencyLike")  String competencyLike, @Param("parentCompetencyLike")  String parentCompetencyLike,   @Param("companyId")  String companyId);
	 
	 @Query("select q from Question q where q.multipleCompetenciesAssociatedWithQuestion like :competency and q.multipleCompetenciesAssociatedWithQuestion like :parentCompetency and q.questionType=:questionType and q.companyId=:companyId and (q.softDelete is null or q.softDelete=false)")
	 List<Question> findAllQuestionsAssociatedWithMultipleCompetenciesByCompetencyForCompany(@Param("questionType")  String questionType, @Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId);
	 
	 
}
