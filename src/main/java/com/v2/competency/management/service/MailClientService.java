package com.v2.competency.management.service;

import java.util.List;
import java.util.Map;

public interface MailClientService {
	
	public void sendMail(List<String> to, List<String> cc, Long templateId, Map<String, String> vars, String subject);

}
