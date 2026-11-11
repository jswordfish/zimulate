package com.v2.competency.management.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v2.competency.management.entities.FailedAttemptSession;

@Repository
public interface FailedAttemptSessionRepository extends JpaRepository<FailedAttemptSession, Long> {

    Optional<FailedAttemptSession> findBySessionId(Long sessionId);
    

}
