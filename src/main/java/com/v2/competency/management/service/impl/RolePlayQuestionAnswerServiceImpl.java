package com.v2.competency.management.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v2.competency.management.entities.RolePlayQuestionAnswer;
import com.v2.competency.management.repos.RolePlayQuestionAnswerRepo;
import com.v2.competency.management.service.RolePlayQuestionAnswerService;

@Service
public class RolePlayQuestionAnswerServiceImpl implements RolePlayQuestionAnswerService {
	
	@Autowired
	RolePlayQuestionAnswerRepo repo;

	@Override
	public List<RolePlayQuestionAnswer> findAllQAForUser(String testName, String email, String companyId,
			Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findAllQAForUser(testName, email, companyId, attempt);
	}

	@Override
	public List<String> findAllQuesTextForUser(String testName, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findAllQuesTextForUser(testName, email, companyId, attempt);
	}

	@Override
	public List<String> findAllAnsTextForUser(String testName, String email, String companyId, Integer attempt) {
		// TODO Auto-generated method stub
		return repo.getAllAnswersForRolePlayTestForUser(testName, email, companyId, attempt);
	}

}
