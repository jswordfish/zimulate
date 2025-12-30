package com.v2.competency.management.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.v2.competency.management.common.util.MailJetEmailClient;
import com.v2.competency.management.service.MailClientService;
@Service
public class MailClientServiceImpl implements MailClientService{

	@Override
	public void sendMail(List<String> to, List<String> cc, Long templateId, Map<String, String> vars, String subject) {
		// TODO Auto-generated method stub
		MailJetEmailClient client = new MailJetEmailClient(to, cc, templateId, vars, subject);
		client.run();
	}

}
