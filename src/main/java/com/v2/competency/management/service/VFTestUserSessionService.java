package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VFTestUserSession;

public interface VFTestUserSessionService {
	
	public VFTestUserSession finfVFTestUserSessionByEmail(String email, String companyId, String testIdentifier, Integer attempt);
	
	public VFTestUserSession saveOrUpdate(VFTestUserSession session);
	
	public Integer findCountOfSessionsForUserFotTest(String email, String companyId, String testIdentifier);
	
	public Page<VFTestUserSession> findUserSessionsForTest(  String testIdentifier, String companyId, Pageable pageable);
	
	public Page<VFTestUserSession> findUserSessionsByEmail(  String email,  String companyId, Pageable pageable);
	
	public List<VFTestUserSession> findAllUserSessionsByEmail(@Param("email")  String email, @Param("companyId") String companyId);

}
