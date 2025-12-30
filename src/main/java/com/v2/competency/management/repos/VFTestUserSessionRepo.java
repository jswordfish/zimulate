package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VFTestUserSession;

public interface VFTestUserSessionRepo extends CrudRepository<VFTestUserSession, Long>{
	
	
	@Query("select v from VFTestUserSession v where v.email=:email and v.companyId=:companyId and v.testIdentifier=:testIdentifier and v.attempt=:attempt")
	public VFTestUserSession findVFTestUserSessionByEmail(@Param("email") String email, @Param("companyId")  String companyId, @Param("testIdentifier")  String testIdentifier, @Param("attempt") Integer attempt);
	
	@Query("select count(v) from VFTestUserSession v where v.email=:email and v.companyId=:companyId and v.testIdentifier=:testIdentifier")
	public Integer findCountOfSessionsForUserFotTest(@Param("email") String email, @Param("companyId") String companyId, @Param("testIdentifier") String testIdentifier);
	
	@Query("select v from VFTestUserSession v where v.companyId=:companyId and v.testIdentifier=:testIdentifier")
	public Page<VFTestUserSession> findUserSessionsForTest(@Param("testIdentifier")  String testIdentifier, @Param("companyId") String companyId, Pageable pageable);

	
	@Query("select v from VFTestUserSession v where v.companyId=:companyId and v.email=:email")
	public Page<VFTestUserSession> findUserSessionsByEmail(@Param("email")  String email, @Param("companyId") String companyId, Pageable pageable);
	
	
	@Query("select v from VFTestUserSession v where v.companyId=:companyId and v.email=:email")
	public List<VFTestUserSession> findAllUserSessionsByEmail(@Param("email")  String email, @Param("companyId") String companyId);

}
