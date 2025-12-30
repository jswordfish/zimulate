package com.v2.competency.management.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VideoAgentSession;

public interface VideoAgentSessionRepo extends CrudRepository<VideoAgentSession, Long> {
	
	@Query("select w from VideoAgentSession w where w.companyId=:companyId and w.videoAgent.id=:videoAgentId and w.email=:email and w.attempt=:attempt")
	public VideoAgentSession findUniqueVideoAgentSession( @Param("videoAgentId") Long videoAgentId , @Param("email")  String email,  @Param("companyId")  String companyId, @Param("attempt")  Integer attempt);
	
	@Query("select count(w) from VideoAgentSession w where w.companyId=:companyId and w.videoAgent.id=:videoAgentId and w.email=:email")
	public Integer findNumberOfAttempts(@Param("videoAgentId") Long videoAgentId , @Param("email")  String email,  @Param("companyId")  String companyId);
	
	@Query("select w from VideoAgentSession w where w.companyId=:companyId")
	public Page<VideoAgentSession> findAllVideoAgentSessionByCompanyId(@Param("companyId") String companyId, Pageable pageable);

	 
	@Query("select w from VideoAgentSession w where w.companyId=:companyId and w.email=:email")
	public Page<VideoAgentSession> findAllVideoAgentSessionForUserByCompanyId(@Param("companyId") String companyId, @Param("email")  String email, Pageable pageable);
	
//	@Query("select w from VideoAgentSession w where w.companyId=:companyId and w.email=:email and w.workflow.id=:workFlowId")
//	public Page<VideoAgentSession> findAllVideoAgentSessionForUserForWorkflowByCompanyId(@Param("companyId") String companyId, @Param("email")  String email, @Param("workFlowId") Long workFlowId, Pageable pageable);

}
