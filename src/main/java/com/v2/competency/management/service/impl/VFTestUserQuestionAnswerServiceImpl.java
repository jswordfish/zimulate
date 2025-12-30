package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.MCQScoreForUserDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.repos.VFTestUserQuestionAnswerRepo;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;

@Service
@Transactional
public class VFTestUserQuestionAnswerServiceImpl implements VFTestUserQuestionAnswerService {
	@Autowired
	VFTestUserQuestionAnswerRepo repo;
	
	@Autowired
	VFTestUserSessionService service;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public VFTestUserQuestionAnswer create(VFTestUserQuestionAnswer questionAnswer) {
		// TODO Auto-generated method stub
		Integer att = service.findCountOfSessionsForUserFotTest(questionAnswer.getEmail(), questionAnswer.getCompanyId(), questionAnswer.getTestIdentifier()) + 1;
		VFTestUserQuestionAnswer ans =  repo.findUniqueQuestionAnswerByEmail(questionAnswer.getTestIdentifier(), questionAnswer.getEmail(), questionAnswer.getCompanyId(), att, questionAnswer.getQid());
			if(ans != null) {
				Long id = ans.getId();
				Date createDate = ans.getCreateDate();
				questionAnswer.setAttempt(att);
				questionAnswer.setCreateDate(createDate);
				questionAnswer.setId(id);
				mapper.map(questionAnswer, ans);
				return repo.save(ans);
				
			}
			else {
				questionAnswer.setAttempt(att);
				questionAnswer.setCreateDate(new Date());
				return repo.save(questionAnswer);
			}
		
	}

	@Override
	public List<VFTestUserQuestionAnswer> findAllQAForUser(String testIdentifier, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findAllQAForUser(testIdentifier, email, companyId, attempt);
	}

	@Override
	public List<String> findAllQuesTextForUser(String testIdentifier, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findAllQuesTextForUser(testIdentifier, email, companyId, attempt);
	}

	@Override
	public List<VFTestUserQuestionAnswer> findMCQQAForUserByCompetency(String parentCompetency, String competency,
			String testIdentifier, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findMCQQAForUserByCompetency(parentCompetency, competency, testIdentifier, email, companyId, attempt);
	}

	@Override
	public List<VFTestUserQuestionAnswer> findSubjectiveQAForUserByCompetency(String parentCompetency,
			String competency, String testIdentifier, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findSubjectiveQAForUserByCompetency(parentCompetency, competency, testIdentifier, email, companyId, attempt);
	}

	@Override
	public VFTestUserQuestionAnswer getById(Long id) {
		// TODO Auto-generated method stub
		return repo.findById(id).get();
	}

	@Override
	public VFTestUserQuestionAnswer findMCQPresenceForTest(String testIdentifier, String companyId) {
		// TODO Auto-generated method stub
		return repo.findMCQPresenceForTest(testIdentifier, companyId);
	}

	@Override
	public List<VFTestUserQuestionAnswer> findDistincyScenarioQsForTest(String testIdentifier, String companyId) {
		// TODO Auto-generated method stub
		return repo.findDistincyScenarioQsForTest(testIdentifier, companyId);
	}

	@Override
	public List<VFTestUserQuestionAnswer> findScenarioAnswersForTestQuestion(String question, String testIdentifier,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findScenarioAnswersForTestQuestion(question, testIdentifier, companyId);
	}

	@Override
	public List<MCQScoreForUserDto> findMCQScoreForUserByAssessment(String testIdentifier, 
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findMCQScoreForUserByAssessment2(testIdentifier, companyId);
	}

	@Override
	public List<VFTestUserQuestionAnswer> findAllSubjectiveAnswersForTest(String testIdentifier, String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllSubjectiveAnswersForTest(testIdentifier, companyId);
	}

}
