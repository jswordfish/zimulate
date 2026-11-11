package com.v2.competency.management.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.v2.competency.management.entities.FailedAttemptSession;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.repos.FailedAttemptSessionRepository;
import com.v2.competency.management.service.FailedAttemptSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FailedAttemptSessionServiceImpl implements FailedAttemptSessionService {

    private final FailedAttemptSessionRepository failedAttemptSessionRepository;

    @Override
    public FailedAttemptSession saveFailedAttemptSession(FailedAttemptSession failedAttemptSession) {
        return failedAttemptSessionRepository.save(failedAttemptSession);
    }

    @Override
    public void deleteFailedAttemptSession(Long sessionId) {

        if (sessionId != null) {
            Optional<FailedAttemptSession> failedAttemptSession =
                    failedAttemptSessionRepository.findBySessionId(sessionId);

            failedAttemptSession.ifPresent(failedAttemptSessionRepository::delete);
        }
    }
}
