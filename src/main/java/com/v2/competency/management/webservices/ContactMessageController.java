package com.v2.competency.management.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.entities.ContactMessage;
import com.v2.competency.management.service.ContactMessageService;

@RestController
public class ContactMessageController {
	
	@Autowired
	ContactMessageService service;
	
	@PostMapping("/saveMessage")
    public ResponseEntity<ContactMessage> submitContactForm(@RequestBody ContactMessage contactMessage) {
        ContactMessage saved = service.saveMessage(contactMessage);
        return ResponseEntity.ok(saved);
    }

}
