package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.AssessmentTraversalPath;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.UserWithTestDto;
import com.v2.competency.management.entities.AssessmentMapper;

public interface AssessmentMapperService {
	
	public AssessmentMapper findByEmailAndTestNameAndPaths( @Param("email") String email,@Param("testName") String testName, @Param("path1") String path1,@Param("path2") String path2,@Param("path3") String path3,@Param("path4") String path4,@Param("path5") String path5, @Param("companyId") String companyId);

	public Page<AssessmentMapper> findAssessmentsForUser(String companyId, String email, Pageable pageable);
	 
	
	public List<AssessmentMapper> findAssessmentsForUserByPath1( @Param("email") String email,@Param("path1") String path1, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2, @Param("companyId") String companyId);


	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3( @Param("email") String email,@Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("companyId") String companyId);


	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("companyId") String companyId);

	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4( @Param("email") String email, @Param("path1") String path1,@Param("path2") String path2 ,@Param("path3") String path3, @Param("path4") String path4, @Param("path5") String path5, @Param("companyId") String companyId);

	public AssessmentMapper saveOrUpdate(AssessmentMapper assessmentMapper);
	
	
	public AssessmentTraversalPath computeForUser(String email, String companyId);
	
	
	public Page<String> getListOfUsersAssignedTests(String companyId, Pageable pageable);
	
	
	public List<AssessmentMapper> findRolesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> findCompetenciesForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> findJobDescriptionsForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> find360DegreeSurveysForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	/**
	 * Added for consolidated Assessments
	 * @param email
	 * @param companyId
	 * @return
	 */
	public List<AssessmentMapper> findConsolidatedAssessmentsForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	/**
	 * Added for consolidated Assessments
	 * @param email
	 * @param companyId
	 * @return
	 */
	public List<AssessmentMapper> findConsolidated360DegreeSurveyForUserAssignedAssessments( @Param("email") String email, @Param("companyId") String companyId);
	
	public List<AssessmentMapper> findConsolidatedReviewers( String testName,  String reviewed_user_email,  String type_path2,  String companyId);
	
	
	public Page<AssessmentMapper> findExternalAssessmentTakers( String companyId, Pageable pageable);
	
	 public Page<AssessmentMapper> findInternalAssessmentTakers(String companyId, Pageable pageable);
	
	 public Page<AssessmentMapper> findExternalAndInternalAssessmentTakers(String companyId, Pageable pageable);
	 
	 public Page<UserWithTestDto> getUsersByTestName(String testName, int page);
	 
	 PaginatedResponseDto findAssessmentsForUserWithFilters(
		        String companyId,
		        String email,
		        String search,
		        String sort,
		        int page,
		        int size);
	 
	 PaginatedResponseDto getTestsAssignedByManager(
	            String assignedBy, String companyId, int page, int size, String sort);
	 
}
