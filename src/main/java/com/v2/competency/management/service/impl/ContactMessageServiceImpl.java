package com.v2.competency.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v2.competency.management.entities.ContactMessage;
import com.v2.competency.management.repos.ContactMessageRepository;
import com.v2.competency.management.service.ContactMessageService;

@Service
public class ContactMessageServiceImpl implements ContactMessageService  {
	
	@Autowired
	ContactMessageRepository repo;

	@Override
	public ContactMessage saveMessage(ContactMessage contactMessage) {
		// TODO Auto-generated method stub
		return repo.save(contactMessage);
	}

}
