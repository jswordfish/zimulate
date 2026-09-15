package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.VFRolePlayTestSession;


public interface VFRolePlayTestSessionService {
	
	public VFRolePlayTestSession findVFRolePlayTestSessionByEmail(String email, String companyId, String testIdentifier, Integer attempt);
	
	public VFRolePlayTestSession findVFRolePlayTestSessionById(Long id);
	
	public VFRolePlayTestSession saveOrUpdate(VFRolePlayTestSession session);
	
	public Integer findCountOfSessionsForUserForTest(String email, String companyId, String testName);
	
	public Page<VFRolePlayTestSession> findUserSessionsForTest(  String testIdentifier, String companyId, Pageable pageable);
	
	Page<VFRolePlayTestSession> findUserSessionsByEmailAndTestName(
	        String email, String companyId, String testName, Pageable pageable);
	
	Page<VFRolePlayTestSession> getAllTestSessions(Pageable pageable);
	
	public Page<VFRolePlayTestSession> findUserSessions(
	        String email,
	        String companyId,
	        String testName,
	        List<String> personaList,
	        boolean isPersonasEmpty,
	        String difficultyLevel,  
	        Pageable pageable);
	
	public List<VFRolePlayTestSession> seatchEmailUserSessionsForTest(String testIdentifier, String companyId,  String containsText);
	
	public List<VFRolePlayTestSession> findRoleplaySessionsByWorkflowId(Long workflowSessionId);
	
	public Page<VFRolePlayTestSession> searchAssessmentsForRoleplay( String companyId, String testIdentifier, String search, Pageable pageable);

	public PaginatedResponseDto getLatestRolePlayAttempts(
	        String companyId,
	        String email,
	        String testName,
	        String search,
	        int page,
	        int size);

}
