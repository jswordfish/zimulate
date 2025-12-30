package com.v2.competency.management.service.impl;

import org.springframework.stereotype.Service;

import com.googlecloud.vertex.ai.insights.dto.RolePlayProcessingRequest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Service
public class QueueManager {

    private final BlockingQueue<RolePlayProcessingRequest> dataQueue = new LinkedBlockingQueue<>();

    public BlockingQueue<RolePlayProcessingRequest> getDataQueue() {
        return dataQueue;
    }

    public synchronized void enqueue(RolePlayProcessingRequest item) {
        try {
        	System.out.println("Adding to queue: Session ID = " + item.getSession().getId());
            dataQueue.put(item);
            System.out.println("Added to queue: Session ID = " + item.getSession().getId());
            System.out.println("Queue size after enqueue: " + dataQueue.size());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error while adding to queue: " + e.getMessage());
        }
    }

    public synchronized void removeFromQueue(RolePlayProcessingRequest request) {
        dataQueue.remove(request);
        System.out.println("Removed session from queue: " + request.getSession().getId());
        System.out.println("Queue size after removal: " + dataQueue.size());
    }

    
    public Integer getPositionInQueue(VFRolePlayTestSession session) {
        return getPosition(this.dataQueue, session);
    }

    public static Integer getPosition(BlockingQueue<RolePlayProcessingRequest> queue, VFRolePlayTestSession targetSession) {
        int position = 0;
        Iterator<RolePlayProcessingRequest> iterator = queue.iterator();
        
        while (iterator.hasNext()) {
            RolePlayProcessingRequest request = iterator.next();
            if (request.getSession().equals(targetSession)) { // Compare sessions
                return position;
            }
            position++;
        }
        
        return -1; // Object not found
    }
    }

