package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.v2.competency.management.dtos.UserTestSessionComputeScoreDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.UserCompetencyWiseScoreForAssessment;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.repos.UserCompetencyWiseScoreForAssessmentRepo;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.UserCompetencyWiseScoreForAssessmentService;
import com.v2.competency.management.service.VFTestService;
@Service
@Transactional
public class UserCompetencyWiseScoreForAssessmentServiceImpl implements UserCompetencyWiseScoreForAssessmentService{

	@Autowired
	UserCompetencyWiseScoreForAssessmentRepo repo;
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	
	@Override
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment(String email, String testName,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findRecordsForUserAssessment(email, testName, companyId);
	}

	@Override
	public void addUserCompetencyWiseScoreForCompetency(UserCompetencyWiseScoreForAssessment forAssessment) {
		// TODO Auto-generated method stub
		VFTest test = testService.findByTestIdentifier(forAssessment.getTestIdentifier(), forAssessment.getCompanyId());
		System.out.println("test identifier is "+forAssessment.getTestIdentifier()+" name "+forAssessment.getTestName());
		String path1 = test.getPath1()==null?"NA":test.getPath1();
		String path2 = test.getPath2()==null?"NA":test.getPath2();
		String path3 = test.getTestName();
		String path4 = "AI Platform";
		String path5 = "NA";
		forAssessment.setTestName(test.getTestName());
		AssessmentMapper assessmentMapper =  assessmentMapperService.findByEmailAndTestNameAndPaths(forAssessment.getEmail(), test.getTestName(), path1, path2, path3, path4, path5, forAssessment.getCompanyId());
		forAssessment.setAssessmentMapper(assessmentMapper);
		forAssessment.setCreateDate(new Date());
		repo.save(forAssessment);
	}

	@Override
	public UserCompetencyWiseScoreForAssessment findUniqueRecord(String email, String testName, String testIdentifier,
			String competency, String parentCompetency, Integer attempt, String questionMode, String companyId) {
		// TODO Auto-generated method stub
		return repo.findUniqueRecord(email, testName, testIdentifier, competency, parentCompetency, attempt, questionMode, companyId);
	}

	@Override
	public Page<UserCompetencyWiseScoreForAssessment> findRecordsByCompanyIdAndquestionType(String questionMode,
			String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findRecordsByCompanyIdAndquestionType(questionMode, companyId, pageable);
	}

	@Override
	public void updateAIScore(Long id, Float aiScore) {
		UserCompetencyWiseScoreForAssessment rec = repo.findById(id).get();
		rec.setAiScore(aiScore);
		repo.save(rec);
	}

	@Override
	public void updateReviewerScore(Long id, Float reviewerScore) {
		UserCompetencyWiseScoreForAssessment rec = repo.findById(id).get();
		rec.setReviewerScore(reviewerScore);
		/**
		 * Reviewer Score will be the final acerage_score at UserCompetencyWiseScoreForAssessment level. So we will update it as well
		 */
		rec.setAverageScore(reviewerScore);
		repo.save(rec);
	}

	@Override
	public List<UserTestSessionComputeScoreDto> findTestScores(String companyId) {
		// TODO Auto-generated method stub
		return repo.findTestScores(companyId);
	}

	@Override
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2(String email, String testIdentifier,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findRecordsForUserAssessment2(email, testIdentifier, companyId);
	}

	@Override
	public Page<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyId(String email, String companyId,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findAllRecordsForUserByCompanyId(email, companyId, pageable);
	}

	@Override
	public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForUserByCompanyIdNoPagination(String email,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllRecordsForUserByCompanyIdNoPagination(email, companyId);
	}

	@Override
	public List<UserCompetencyWiseScoreForAssessment> findRecordsForUserAssessment2Byattempt(String email,
			String testIdentifier, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findRecordsForUserAssessment2Byattempt(email, testIdentifier, companyId, attempt);
	}

	@Override
	public List<UserCompetencyWiseScoreForAssessment> findAllRecordsForAssessmentByCompanyIdNoPagination(
			String testIdentifier, String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllRecordsForAssessmentByCompanyIdNoPagination(testIdentifier, companyId);
	}  

}
