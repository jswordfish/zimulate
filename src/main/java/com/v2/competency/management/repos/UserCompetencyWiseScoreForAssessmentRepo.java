package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.UserTestSessionComputeScoreDto;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;

public interface UserCompetencyWiseScoreForAssessmentRepo extends CrudRepository<UserCompetencyWiseScoreForAssessment, Long> {
	
	
	@Query("select u from UserCompetencyWiseScoreForAssessment u where u.email =:email and u.companyId =:companyId and u.testName =:testName")
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment( @Param("email") String email,@Param("testName") String testName, @Param("companyId") String companyId);
	
	@Query("select u from UserCompetencyWiseScoreForAssessment u where u.email =:email and u.companyId =:companyId and u.testIdentifier =:testIdentifier")
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2( @Param("email") String email,@Param("testIdentifier") String testIdentifier, @Param("companyId") String companyId);
	
	@Query("select u from UserCompetencyWiseScoreForAssessment u where u.email =:email and u.companyId =:companyId and u.testIdentifier =:testIdentifier and u.attempt =:attempt")
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2Byattempt( @Param("email") String email,@Param("testIdentifier") String testIdentifier, @Param("companyId") String companyId, @Param("attempt") Integer attempt);


	@Query("select u from UserCompetencyWiseScoreForAssessment u where u.email =:email and u.companyId =:companyId and u.testName =:testName and u.testIdentifier =:testIdentifier "
			+ "and u.competency =:competency and u.parentCompetency =:parentCompetency and u.attempt =:attempt and u.questionMode =:questionMode")
	public UserCompetencyWiseScoreForAssessment findUniqueRecord(@Param("email") String email,@Param("testName") String testName,@Param("testIdentifier") String testIdentifier,
			@Param("competency") String competency,
			@Param("parentCompetency") String parentCompetency,
			@Param("attempt") Integer attempt,
			@Param("questionMode") String questionMode,
			@Param("companyId") String companyId);
	
	
	@Query("select u from UserCompetencyWiseScoreForAssessment u where  u.companyId =:companyId and u.questionMode =:questionMode")
	public Page<UserCompetencyWiseScoreForAssessment> findRecordsByCompanyIdAndquestionType( @Param("questionMode") String questionMode, @Param("companyId") String companyId, Pageable pageable);

	 @Query("SELECT " +
	           "    new com.v2.competency.management.dtos.UserTestSessionComputeScoreDto(q.testName, q.testIdentifier, q.email, q.attempt, AVG(q.averageScore) ) " +
	           "FROM " +
	           "    UserCompetencyWiseScoreForAssessment q where q.companyId=:companyId  GROUP BY q.testName, q.email, q.attempt")
	List<UserTestSessionComputeScoreDto> findTestScores(  @Param("companyId")  String companyId);
	 
	 
	 @Query("select u from UserCompetencyWiseScoreForAssessment u where  u.companyId =:companyId and u.email =:email")
		public Page<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyId( @Param("email") String email, @Param("companyId") String companyId, Pageable pageable);
	 
	 
	 @Query("select u from UserCompetencyWiseScoreForAssessment u where  u.companyId =:companyId and u.email =:email")
		public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyIdNoPagination( @Param("email") String email, @Param("companyId") String companyId);
	 
	 
	 @Query("select u from UserCompetencyWiseScoreForAssessment u where  u.companyId =:companyId and u.testIdentifier =:testIdentifier")
		public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForAssessmentByCompanyIdNoPagination( @Param("testIdentifier") String testIdentifier, @Param("companyId") String companyId);
	 
	 

}
