package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.User;

public interface AssessmentMapperRepo extends JpaRepository<AssessmentMapper, Long> {
	
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.testName =:testName  and u.path1 =:path1 and  u.path2 =:path2  and  u.path3 =:path3  and  u.path4 =:path4  and  u.path5 =:path5")
	public AssessmentMapper findByEmailAndTestNameAndPaths( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2,@Param("path3") String path3,@Param("path4") String path4,@Param("path5") String path5, @Param("companyId") String companyId);

	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId")
	 public Page<AssessmentMapper> findAssessmentsForUser(@Param("companyId") String companyId,@Param("email") String email, Pageable pageable);
	 
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1 =:path1")
	public List<AssessmentMapper> findAssessmentsForUserByPath1( @Param("email") String email, @Param("path1") String path1, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1 =:path1 and u.path2 =:path2")
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2, @Param("companyId") String companyId);


	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1 =:path1 and u.path2 =:path2 and u.path3 =:path3")
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("companyId") String companyId);


	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1 =:path1 and u.path2 =:path2 and u.path3 =:path3 and u.path4 =:path4")
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("companyId") String companyId);

	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1 =:path1 and u.path2 =:path2 and u.path3 =:path3 and u.path4 =:path4 and u.path5 =:path5")
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("path5") String path5, @Param("companyId") String companyId);

	@Query("select DISTINCT (u.path1)  from AssessmentMapper u where u.email =:email and u.companyId =:companyId")
	public List<String> fetchDistinctPath1sForUser(@Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select DISTINCT (u.path2)  from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1=:path1")
	public List<String> fetchDistinctPath2sForUser(@Param("email") String email, @Param("companyId") String companyId, @Param("path1") String path1);
	
	@Query("select DISTINCT (u.path3)  from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1=:path1 and u.path2=:path2")
	public List<String> fetchDistinctPath3sForUser(@Param("email") String email, @Param("companyId") String companyId, @Param("path1") String path1, @Param("path2") String path2);
	
	@Query("select DISTINCT (u.path4)  from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1=:path1 and u.path2=:path2 and u.path3=:path3")
	public List<String> fetchDistinctPath4sForUser(@Param("email") String email, @Param("companyId") String companyId, @Param("path1") String path1, @Param("path2") String path2, @Param("path3") String path);
	
	
	@Query("select DISTINCT (u.path4)  from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.path1=:path1 and u.path2=:path2 and u.path3=:path3  and u.path4=:path4")
	public List<String> fetchDistinctPath5sForUser(@Param("email") String email, @Param("companyId") String companyId, @Param("path1") String path1, @Param("path2") String path2, @Param("path3") String path, @Param("path4") String path4);
	
	@Query(nativeQuery = true, value="select DISTINCT(u.email)  from assessment_mapper u where u.company_id =:companyId")
	public Page<String> getListOfUsersAssignedTests(@Param("companyId") String companyId, Pageable pageable);
	
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.typePath1='Roles' and (u.reviewMode=false OR  u.reviewMode IS NULL) and (u.consolidatedAssessments IS NULL OR u.consolidatedAssessments=false)")
	public List<AssessmentMapper> findRolesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.typePath1='Competency' and (u.reviewMode=false  OR  u.reviewMode IS NULL) and (u.consolidatedAssessments IS NULL OR u.consolidatedAssessments=false)")
	public List<AssessmentMapper> findCompetenciesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.typePath1='Job Description' and (u.reviewMode=false  OR  u.reviewMode IS NULL) and (u.consolidatedAssessments IS NULL OR u.consolidatedAssessments=false)")
	public List<AssessmentMapper> findJobDescriptionsForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where  u.companyId =:companyId and u.typePath1='Competency' and u.reviewMode=true and u.reviewedUserEmail=:email and (u.consolidatedAssessments IS NULL OR u.consolidatedAssessments=false)")
	public List<AssessmentMapper> find360DegreeSurveysForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where u.email =:email and u.companyId =:companyId and u.typePath1='Consolidated Assessments' and (u.reviewMode=false OR  u.reviewMode IS NULL) and (u.consolidatedAssessments=true)")
	public List<AssessmentMapper> findConsolidatedAssessmentsForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	@Query("select u from AssessmentMapper u where  u.companyId =:companyId and u.typePath1='Consolidated Assessments' and u.reviewMode=true and u.reviewedUserEmail=:email and (u.consolidatedAssessments=true)")
	public List<AssessmentMapper> findConsolidated360DegreeSurveyForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	
	@Query("select u from AssessmentMapper u where  u.companyId =:companyId and u.testName =:testName  and u.typePath2 =:type_path2 and u.reviewMode=true and u.reviewedUserEmail=:reviewed_user_email ")
	public List<AssessmentMapper> findConsolidatedReviewers(@Param("testName") String testName, @Param("reviewed_user_email") String reviewed_user_email, @Param("type_path2") String type_path2, @Param("companyId") String companyId);
	

	@Query("select u from AssessmentMapper u where u.external = true and u.companyId =:companyId  group by  u.email")
	 public Page<AssessmentMapper> findExternalAssessmentTakers(@Param("companyId") String companyId, Pageable pageable);
	
	@Query("select u from AssessmentMapper u where u.external = false and u.companyId =:companyId  group by  u.email")
	 public Page<AssessmentMapper> findInternalAssessmentTakers(@Param("companyId") String companyId, Pageable pageable);
	
	@Query("select u from AssessmentMapper u where u.companyId =:companyId  group by  u.email")
	 public Page<AssessmentMapper> findExternalAndInternalAssessmentTakers(@Param("companyId") String companyId, Pageable pageable);
	
	@Query("SELECT u FROM User u " +
	           "JOIN AssessmentMapper am ON u.email = am.email " +
	           "WHERE am.testName = :testName")
	    Page<User> findUsersByTestName(@Param("testName") String testName, Pageable pageable);
	
	@Query("SELECT u FROM AssessmentMapper u " +
		       "WHERE u.companyId = :companyId " +
		       "AND u.email = :email " +
		       "AND (:search IS NULL OR LOWER(u.testName) LIKE LOWER(CONCAT('%', :search, '%'))) ")
		Page<AssessmentMapper> findAssessmentsForUserWithFilters(
		        @Param("companyId") String companyId,
		        @Param("email") String email,
		        @Param("search") String search,
		        Pageable pageable);
	
	@Query("SELECT a FROM AssessmentMapper a " +
	           "WHERE (:assignedBy IS NULL OR LOWER(a.assignedBy) = LOWER(:assignedBy)) " +
	           "AND (:companyId IS NULL OR a.companyId = :companyId)")
	    Page<AssessmentMapper> findByAssignedBy(
	            @Param("assignedBy") String assignedBy,
	            @Param("companyId") String companyId,
	            Pageable pageable);
	 
}
