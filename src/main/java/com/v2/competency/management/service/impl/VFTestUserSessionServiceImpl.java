package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.VFTestUserSession;
import com.v2.competency.management.repos.VFTestUserSessionRepo;
import com.v2.competency.management.service.VFTestUserSessionService;
@Service
@Transactional
public class VFTestUserSessionServiceImpl implements VFTestUserSessionService{
	@Autowired
	VFTestUserSessionRepo repo;
	

	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public VFTestUserSession finfVFTestUserSessionByEmail(String email, String companyId, String testIdentifier, Integer attempt) {
		return repo.findVFTestUserSessionByEmail(email, companyId, testIdentifier, attempt);
	}

	@Override
	public VFTestUserSession saveOrUpdate(VFTestUserSession session) {
		Objects.requireNonNull(session);
		Objects.requireNonNull(session.getCompanyId());
		Objects.requireNonNull(session.getEmail());
		Objects.requireNonNull(session.getAttempt());
		
		VFTestUserSession session2 = finfVFTestUserSessionByEmail(session.getEmail(), session.getCompanyId(), session.getTestIdentifier(), session.getAttempt());
		if(session2 == null) {
			session.setCreateDate(new Date());
			return repo.save(session);
		}
		else {
			session.setId(session2.getId());
			session.setCreateDate(session2.getCreateDate());
			session.setUpdateDate(new Date());
			mapper.map(session, session2);
			return repo.save(session2);
		}
		
	}

	@Override
	public Integer findCountOfSessionsForUserFotTest(String email, String companyId, String testIdentifier) {
		return repo.findCountOfSessionsForUserFotTest(email, companyId, testIdentifier);
	}

	@Override
	public Page<VFTestUserSession> findUserSessionsForTest(String testIdentifier, String companyId, Pageable pageable) {
		 return repo.findUserSessionsForTest(testIdentifier, companyId, pageable);
	}

	@Override
	public Page<VFTestUserSession> findUserSessionsByEmail(String email, String companyId, Pageable pageable) {
		return repo.findUserSessionsByEmail(email, companyId, pageable);
	}

	@Override
	public List<VFTestUserSession> findAllUserSessionsByEmail(String email, String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllUserSessionsByEmail(email, companyId);
	}

}
