package com.v2.competency.management.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.v2.competency.management.entities.FailedAttemptSession;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.entities.WorkflowRecommGenerator;
import com.v2.competency.management.repos.FailedAttemptSessionRepository;
import com.v2.competency.management.repos.VFRolePlayTestRepo;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.EmailService;
import com.v2.competency.management.service.FailedAttemptSessionService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;

import lombok.RequiredArgsConstructor;

@Service
@Component
@RequiredArgsConstructor
public class FailedEvaluationRetryScheduler {

    private static final int MAX_ATTEMPTS = 3;
    private static final String ERROR_PREFIX = "Error";
    private static final long DELAY_BETWEEN_SESSIONS_MS = 60 * 1000;

    // Adjust to your actual types / names
    private final VFRolePlayTestSessionRepo sessionRepo;
    private final VFRolePlayTestSessionService rolePlayTestSessionService;
    private final VFRolePlayTestRepo testRepo;
    private final FailedAttemptSessionRepository failedAttemptSessionRepository;
    private final FailedAttemptSessionService failedAttemptSessionService;
    private final GeminiAudioVideoService geminiservice;
    private final ElevenLabsAgentService elevenLabsAgentService;
    private final ASyncAIInsightsGenService aSyncAIInsightsGenService; 
    private final WorkflowRecommGenerator workflowRecommGenerator;
    private final PropertyConfig config;
    private final EmailService emailService;

//    // fixedDelay: next run starts 30 min after the previous one FINISHES (no overlap)
//    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 60 * 1000)
////    @Scheduled(fixedDelay = 30 * 60 * 1000)
//    public void retryFailedEvaluations() {
//        List<VFRolePlayTestSession> sessions = sessionRepo.findSessionsNeedingRetry();
//        System.out.println("Retry scheduler: found " + sessions.size() + " candidate sessions");
//
//        for (VFRolePlayTestSession session : sessions) {
//            try {
//                processSession(session);
//            } catch (Exception e) {
//                // one bad session must not stop the rest
//                System.err.println("Retry scheduler: error on session " + session.getId());
//                e.printStackTrace();
//            }
//        }
//    }
    
 // fixedDelay: next run starts 30 min after the previous one FINISHES (no overlap)
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 60 * 1000)
//    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void retryFailedEvaluations() {
        List<VFRolePlayTestSession> sessions = sessionRepo.findSessionsNeedingRetry();
        System.out.println("Retry scheduler: found " + sessions.size() + " candidate sessions");
 
        // Strictly one session at a time: retry -> get result/error -> wait 1 min -> next
        for (VFRolePlayTestSession session : sessions) {
            boolean attempted;
            try {
                attempted = processSession(session);
            } catch (Exception e) {
                // one bad session must not stop the rest
                System.err.println("Retry scheduler: error on session " + session.getId());
                e.printStackTrace();
                attempted = true; // an error mid-attempt still counts, so pace the next one
            }
 
            // only wait if a Gemini retry was actually made (skipped sessions cost no time)
            if (attempted) {
                try {
                    System.out.println("Retry scheduler: waiting 1 min before next session");
                    Thread.sleep(DELAY_BETWEEN_SESSIONS_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // app shutting down, stop cleanly
                    return;
                }
            }
        }
    }
    
    private void markTriesExhausted(VFRolePlayTestSession session) {
        session.setGeminiTriesExhausted(true);
        session.setEvaluationFailed(true);
        rolePlayTestSessionService.saveOrUpdate(session);
    }

//    private void processSession(VFRolePlayTestSession session) throws IOException {
//        // 1. Decide attempt number
//    	FailedAttemptSession attemptRow = failedAttemptSessionRepository
//    		    .findBySessionId(session.getId())
//    		    .orElse(null);
//
//    	int attemptNumber;
//    	if (attemptRow == null) {
//    	    attemptNumber = 2;
//    	} else if (Boolean.TRUE.equals(attemptRow.getSuccess())) {
//    	    return; // already succeeded
//    	} else if (attemptRow.getGeminiAttempt() == null) {
//    	    return;
//    	} else if (attemptRow.getGeminiAttempt() >= MAX_ATTEMPTS) {
//    	    markTriesExhausted(session); // retries used up -> stop picking this session up
//    	    return;
//    	} else {
//    	    attemptNumber = attemptRow.getGeminiAttempt() + 1; // 2 -> 3
//    	}
//
//        // 2. Find the test (testIdentifier == testName)
//        VFRolePlayTest test = testRepo.findRolePlayTestsByTestName(
//                session.getCompanyId(), session.getTestIdentifier());
//
//        if (test == null) {
//            System.err.println("Retry scheduler: test not found for session " + session.getId());
//            markStillFailed(session);
//            recordAttempt(attemptRow, session.getId(), attemptNumber, false);
//            return;
//        }
//
//        // 3. Build prompt (same as normal flow)
//        String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptEasy();
//        prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
//        prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
//        prompt = prompt.replace("${ANALYSIS_JSON}", aSyncAIInsightsGenService.getResultInputJson(test));
//
//        String location = config.getGeminiLocation();
//        String model = config.getGeminiModelName();
//
//        // 4. Call the correct Gemini method
//        String insights;
//        try {
//            if (Boolean.TRUE.equals(test.getIsTranscriptBasedAnalysis())) {
//                String transcript = elevenLabsAgentService.getConversationTranscript(session.getConversationId());
//                insights = geminiservice.transcriptInput(location, model, prompt, transcript);
//            } else {
//                insights = geminiservice.videoInputWithGoogleCloudBucketUrl(
//                        location, model, prompt, session.getVideoLink()); // videoLink holds the bucket path
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            insights = null;
//        }
//
//        // 5. Clean + validate (Gemini methods return "Error during..." instead of throwing)
//        if (insights != null) {
//            insights = insights.replaceFirst("```json\\n", "").replaceFirst("\\n```", "");
//        }
//        boolean success = insights != null
//                && !insights.trim().isEmpty()
//                && !insights.trim().startsWith(ERROR_PREFIX);
//
//        // 6. Persist
//        if (success) {
//            session.setVideoInsightsJson(insights);
//            session.setEvaluationFailed(false);
//            session.setReportsVersion(test.getReportVersion());
//            session = rolePlayTestSessionService.saveOrUpdate(session);
//
//            recordAttempt(attemptRow, session.getId(), attemptNumber, true);
//
//            // Same follow-up as the normal flow (email intentionally not re-sent)
//            if (workflowRecommGenerator.checkIfRecommCanBeGenerated(
//                    session.getId(), session.getWorkflowSessionId())) {
//                workflowRecommGenerator.generateRecommendations(session.getWorkflowSessionId());
//            }
//            System.out.println("Retry scheduler: session " + session.getId()
//                    + " succeeded on attempt " + attemptNumber);
//        } else {
//            markStillFailed(session);
//            recordAttempt(attemptRow, session.getId(), attemptNumber, false);
//            System.out.println("Retry scheduler: session " + session.getId()
//                    + " failed again on attempt " + attemptNumber);
//        }
//    }
    
 

    private boolean processSession(VFRolePlayTestSession session) throws IOException {
    	
    	
        VFRolePlayTest test = testRepo.findRolePlayTestsByTestName(
                session.getCompanyId(), session.getTestIdentifier());

        if (test != null && !"V2".equalsIgnoreCase(test.getReportVersion())) {
            System.out.println("Retry scheduler: skipping session " + session.getId()
                    + " (reportVersion=" + test.getReportVersion() + ", not V2)");
            return false; 
        }
    	
    	
        // 1. Decide attempt number
    	FailedAttemptSession attemptRow = failedAttemptSessionRepository
    		    .findBySessionId(session.getId())
    		    .orElse(null);
 
    	int attemptNumber;
    	if (attemptRow == null) {
    	    attemptNumber = 2;
    	} else if (Boolean.TRUE.equals(attemptRow.getSuccess())) {
    	    return false; // already succeeded
    	} else if (attemptRow.getGeminiAttempt() == null) {
    	    return false;
    	} else if (attemptRow.getGeminiAttempt() >= MAX_ATTEMPTS) {
    	    markTriesExhausted(session); // retries used up, stop picking this session up
    	    return false;
    	} else {
    	    attemptNumber = attemptRow.getGeminiAttempt() + 1;
    	}
 
        
 
        if (test == null) {
            System.err.println("Retry scheduler: test not found for session " + session.getId());
            markStillFailed(session);
            recordAttempt(attemptRow, session.getId(), attemptNumber, false);
            notifyRetryFailure(session, attemptNumber, "Test not found for testIdentifier "
                    + session.getTestIdentifier());
            return false; // no Gemini call was made
        }
 
        String location = config.getGeminiLocation();
        String model = config.getGeminiModelName();
 
        
        String insights = null;
        String failureReason = null;
        try {
            
            String prompt = test.getRoleplayAnalysisStructure().getAnalysisGenPromptEasy();
            prompt = prompt.replace("${SCENARIO}", test.getQuestionText());
            prompt = prompt.replace("${PARAMETERS}", test.getCommaSeparatedAnalysisParams());
            prompt = prompt.replace("${ANALYSIS_JSON}", aSyncAIInsightsGenService.getResultInputJson(test));
 
            
            if (Boolean.TRUE.equals(test.getIsTranscriptBasedAnalysis())) {
                String transcript = elevenLabsAgentService.getConversationTranscript(session.getConversationId());
                insights = geminiservice.transcriptInput(location, model, prompt, transcript);
            } else {
                insights = geminiservice.videoInputWithGoogleCloudBucketUrl(
                        location, model, prompt, session.getVideoLink()); // videoLink holds the bucket path
            }
        } catch (Exception e) {
            e.printStackTrace();
            insights = null;
            failureReason = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
        }
 
        
        boolean success = insights != null
                && !insights.trim().isEmpty()
                && !insights.trim().startsWith(ERROR_PREFIX);
 
        if (success) {
            insights = insights.replaceFirst("```json\\n", "").replaceFirst("\\n```", "");
        }
 
        if (success) {
            session.setVideoInsightsJson(insights);
            session.setEvaluationFailed(false);
            session.setReportsVersion(test.getReportVersion());
            session = rolePlayTestSessionService.saveOrUpdate(session);
 
            recordAttempt(attemptRow, session.getId(), attemptNumber, true);
 
            if (workflowRecommGenerator.checkIfRecommCanBeGenerated(
                    session.getId(), session.getWorkflowSessionId())) {
                workflowRecommGenerator.generateRecommendations(session.getWorkflowSessionId());
            }
            System.out.println("Retry scheduler: session " + session.getId()
                    + " succeeded on attempt " + attemptNumber);
        } else {
            if (failureReason == null) {
                failureReason = (insights == null || insights.trim().isEmpty())
                        ? "No insights received from Gemini"
                        : insights; 
            }
 
            markStillFailed(session);
            recordAttempt(attemptRow, session.getId(), attemptNumber, false);
            notifyRetryFailure(session, attemptNumber, failureReason);
            System.out.println("Retry scheduler: session " + session.getId()
                    + " failed again on attempt " + attemptNumber);
        }
 
        return true; // a Gemini retry was made
    }

    private void notifyRetryFailure(VFRolePlayTestSession session, int geminiAttemptNumber, String reason) {
        try {
            emailService.sendReportGenerationFailedEmail(
                    session.getId(),
                    session.getEmail(),
                    session.getTestIdentifier(),
                    session.getAttempt(),
                    session.getCompanyId(),
                    "Retry attempt " + geminiAttemptNumber + " failed: " + reason);
        } catch (Exception e) {
            System.err.println("Retry scheduler: failed to send failure email for session " + session.getId());
            e.printStackTrace();
        }
    }

    private void markStillFailed(VFRolePlayTestSession session) {
        if (!Boolean.TRUE.equals(session.getEvaluationFailed())) {
            session.setEvaluationFailed(true);
            rolePlayTestSessionService.saveOrUpdate(session);
        }
    }

    private void recordAttempt(FailedAttemptSession existing, Long sessionId, int attempt, boolean success) {
        FailedAttemptSession row = existing != null ? existing : new FailedAttemptSession();
        row.setSessionId(sessionId);
        row.setGeminiAttempt(attempt);
        row.setSuccess(success);
        failedAttemptSessionService.saveFailedAttemptSession(row);
    }
}


