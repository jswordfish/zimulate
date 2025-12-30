package com.v2.competency.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import com.googlecloud.vertex.ai.insights.dto.RolePlayProcessingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.v2.competency.management.entities.VFRolePlayTestSession;

import com.v2.competency.management.service.ASyncAIInsightsGenService;
import com.v2.competency.management.service.SyncAIInsightsGenService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;


@Service
public class QueueListenerThread {

    private static final Logger logger = LoggerFactory.getLogger(QueueListenerThread.class);

    private Thread thread;
    private final BlockingQueue<RolePlayProcessingRequest> queue;
    private final QueueManager queueManager;
    private final VFRolePlayTestSessionService rolePlayTestSessionService;
    private final SyncAIInsightsGenService syncAIInsightsGenService;

    @Autowired
    public QueueListenerThread(QueueManager queueManager, 
                               VFRolePlayTestSessionService rolePlayTestSessionService,
                               SyncAIInsightsGenService syncAIInsightsGenService) {
        this.queue = queueManager.getDataQueue();
        this.queueManager = queueManager;
        this.rolePlayTestSessionService = rolePlayTestSessionService;
        this.syncAIInsightsGenService = syncAIInsightsGenService;
    }

    @PostConstruct
    public void startListener() {
        System.out.println("QueueListenerThread is starting...");

        this.thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("Waiting for next item...");
                    RolePlayProcessingRequest item = queue.take(); // Blocking call
                    System.out.println("Processing insights for session ID: " + item.getSession().getId());

                    processInsights(item);

                    queueManager.removeFromQueue(item);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Listener thread interrupted.");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Unexpected error in queue processing", e);
                }
            }
        });

        this.thread.setDaemon(true);
        this.thread.start();
    }

    private void processInsights(RolePlayProcessingRequest request) {
        try {
            VFRolePlayTestSession session = request.getSession();
            if (session == null) {
                System.out.println("Session is null. Skipping.");
                return;
            }

            System.out.println("Processing insights for session ID: " + session.getId() + " | Video Path: " + session.getVideoLink());
            
            syncAIInsightsGenService.generateInsightsForRolePlayBasedAssessmentWithVideoInAsyncDJ(
                session.getTestName(), session.getEmail(), session.getCompanyId(), session, request.getTranscript(), session.getVideoLink()
            );

            System.out.println("Completed processing insights for session ID: " + session.getId());

        } catch (Exception e) {
            System.err.println("Error processing insights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopListener() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}