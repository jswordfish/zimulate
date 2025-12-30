package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.dtos.UserTestSessionComputeScoreDto;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;

public interface UserCompetencyWiseScoreForAssessmentService {
	
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment( String email, String testName,String companyId);
	
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2( String email,String testIdentifier, String companyId);
	
	public void addUserCompetencyWiseScoreForCompetency(UserCompetencyWiseScoreForAssessment forAssessment);
	
	public UserCompetencyWiseScoreForAssessment findUniqueRecord(String email,String testName,String testIdentifier,
			 String competency,
			 String parentCompetency,
			Integer attempt,
			 String questionMode,
			 String companyId);

	public Page<UserCompetencyWiseScoreForAssessment> findRecordsByCompanyIdAndquestionType(  String questionMode, String companyId, Pageable pageable);
	
	public void updateAIScore(Long id, Float aiScore);
	
	public void updateReviewerScore(Long id, Float reviewerScore);
	
	List<UserTestSessionComputeScoreDto> findTestScores(  String companyId);
	
	
	public Page<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyId(String email,  String companyId, Pageable pageable);
	
	
	public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyIdNoPagination( String email, String companyId);
	
	
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2Byattempt( String email, String testIdentifier,  String companyId,  Integer attempt);

	public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForAssessmentByCompanyIdNoPagination(  String testIdentifier,  String companyId);
	 
	
}
