package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VFRolePlayTestSession;


public interface VFRolePlayTestSessionRepo extends JpaRepository<VFRolePlayTestSession, Long> {
	
	@Query("select v from VFRolePlayTestSession v where v.email=:email and v.companyId=:companyId and v.testIdentifier=:testIdentifier and v.attempt=:attempt")
	public VFRolePlayTestSession findVFRolePlayTestSessionByEmail(@Param("email") String email, @Param("companyId")  String companyId, @Param("testIdentifier")  String testIdentifier, @Param("attempt") Integer attempt);
	
	@Query("select count(v) from VFRolePlayTestSession v where v.email=:email and v.companyId=:companyId and v.testIdentifier=:testIdentifier")
	public Integer findCountOfSessionsForUserFotTest(@Param("email") String email, @Param("companyId") String companyId, @Param("testIdentifier") String testIdentifier);
	
	@Query("select count(v) from VFRolePlayTestSession v where v.email=:email and v.companyId=:companyId and v.testName=:testName")
	public Integer findCountOfSessionsForUserFotTestByName(@Param("email") String email, @Param("companyId") String companyId, @Param("testName") String testName);
	
	
	@Query("select v from VFRolePlayTestSession v where v.companyId=:companyId and v.testIdentifier=:testIdentifier")
	public Page<VFRolePlayTestSession> findUserSessionsForTest(@Param("testIdentifier")  String testIdentifier, @Param("companyId") String companyId, Pageable pageable);
	
	@Query("select v from VFRolePlayTestSession v where v.companyId=:companyId and v.testIdentifier=:testIdentifier and LOWER(v.email) LIKE LOWER(CONCAT('%', :containsText, '%'))")
	public List<VFRolePlayTestSession> seatchEmailUserSessionsForTest(@Param("testIdentifier")  String testIdentifier, @Param("companyId") String companyId, @Param("containsText") String containsText);


	
	@Query("SELECT v FROM VFRolePlayTestSession v " +
		       "WHERE v.companyId = :companyId " +
		       "AND v.email = :email " +
		       "AND (:testName IS NULL OR LOWER(v.testName) LIKE LOWER(CONCAT('%', :testName, '%')))")
		Page<VFRolePlayTestSession> findUserSessionsByEmailAndTestName(
		        @Param("email") String email,
		        @Param("companyId") String companyId,
		        @Param("testName") String testName,
		        Pageable pageable);
	
	@Query("SELECT v FROM VFRolePlayTestSession v " +
		       "WHERE v.companyId = :companyId " +
		       "AND v.email = :email " +
		       "AND (:testName IS NULL OR LOWER(v.testName) LIKE LOWER(CONCAT('%', :testName, '%'))) " +
		       "AND (:isPersonasEmpty = true OR LOWER(v.rolePlayPersona) IN (:personaList)) " +
		       "AND (:difficultyLevel IS NULL OR LOWER(v.difficultyLevel) = LOWER(:difficultyLevel))")
		Page<VFRolePlayTestSession> findUserSessions(
		        @Param("email") String email,
		        @Param("companyId") String companyId,
		        @Param("testName") String testName,
		        @Param("personaList") List<String> personaList,
		        @Param("isPersonasEmpty") boolean isPersonasEmpty,
		        @Param("difficultyLevel") String difficultyLevel,
		        Pageable pageable);
	
	@Query("select v from VFRolePlayTestSession v where v.workflowSessionId=:workflowSessionId")
	public List<VFRolePlayTestSession> findRoleplaySessionsByWorkflowSessionId(@Param("workflowSessionId") Long workflowSessionId);
	
	@Query("select v from VFRolePlayTestSession v where v.companyId=:companyId and v.testIdentifier=:testIdentifier and (LOWER(v.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.lastName) LIKE LOWER(CONCAT('%', :search, '%')) )")
	public Page<VFRolePlayTestSession> searchAssessmentsForRoleplay( @Param("companyId")  String companyId, @Param("testIdentifier")  String testIdentifier, @Param("search") String search, Pageable pageable);
	


}
