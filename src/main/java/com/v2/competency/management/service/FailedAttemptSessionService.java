package com.v2.competency.management.service;

import com.v2.competency.management.entities.FailedAttemptSession;
import com.v2.competency.management.entities.VFRolePlayTestSession;

public interface FailedAttemptSessionService {

	FailedAttemptSession saveFailedAttemptSession(FailedAttemptSession failedAttemptSession);

	void deleteFailedAttemptSession(Long sessionId);

}
