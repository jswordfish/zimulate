package com.v2.competency.management.service;

public interface EmailService {
	
	public void sendEmail(String email, String cc[], String subject, String roleplay, Integer attempt, String firstName, String lastName, String persona, String companyId);
	
	public void sendEmailWithtoIgnore(String email, String cc[], String subject, String roleplay, Integer attempt, String firstName, String lastName, String persona, String companyId);


}
