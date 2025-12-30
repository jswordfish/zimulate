package com.v2.competency.management.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.MessageResult;
import com.mailjet.client.transactional.response.SendEmailError;
import com.mailjet.client.transactional.response.SendEmailsResponse;

public class MailJetEmailClient implements Runnable{
	
	List<String> to = new ArrayList<>();
	
	List<String> cc = new ArrayList<>();
	
	Long templateId = 0l;
	
	Map<String, String> variables = new HashMap<>();
	
	String apiKey = "6a8fc48f3ce39258742502defebf6982";
	
	String secret = "3060b1a7e3126e204fde4f612be53de5";
	
	String sender = "jatin.sutaria@thev2technologies.com";
	
	String subject = "default, not set";
	 
	 Logger logger = LoggerFactory.getLogger(MailJetEmailClient.class);
	 
	 

	public MailJetEmailClient(List<String> to, List<String> cc, Long templateId, Map<String, String> variables,
			String subject) {
		super();
		this.to = to;
		if(cc != null) {
			this.cc = cc;
		}
		
		this.templateId = templateId;
			if(variables != null) {
				this.variables = variables;
			}
		
			if(subject != null) {
				this.subject = subject;
			}
		
	}



	public List<String> getTo() {
		return to;
	}



	public void setTo(List<String> to) {
		this.to = to;
	}



	public List<String> getCc() {
		return cc;
	}



	public void setCc(List<String> cc) {
		this.cc = cc;
	}



	public Long getTemplateId() {
		return templateId;
	}



	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}



	public Map<String, String> getVariables() {
		return variables;
	}



	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}



	public String getSender() {
		return sender;
	}



	public void setSender(String sender) {
		this.sender = sender;
	}



	public String getSubject() {
		return subject;
	}



	public void setSubject(String subject) {
		this.subject = subject;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(secret)
                .build();

        MailjetClient client = new MailjetClient(options);
        String msgId = ""+System.currentTimeMillis();
        
        List<SendContact> toList = new ArrayList<>();
        for(String t : to) {
        	System.out.println("sending mail to  "+t);
        	toList.add(new SendContact(t));
        }
        
        List<SendContact> ccList = new ArrayList<>();
        for(String c : cc) {
        	System.out.println("sending mail cc  "+c);
        	ccList.add(new SendContact(c));
        }
        
        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(toList)
                .cc(ccList)
                .from(new SendContact(sender, "BRSF Support"))
                .subject(getSubject())
                .trackOpens(TrackOpens.ENABLED)
                .templateID(getTemplateId()).templateLanguage(true)
                .variables(variables)
                .customID(msgId)
                .build();
        
        SendEmailsRequest request = SendEmailsRequest
                .builder()
                .message(message1) // you can add up to 50 messages per request
                .build();
        
        try {
			SendEmailsResponse response = request.sendWith(client);
			MessageResult[] res = response.getMessages();
				for(MessageResult r : res) {
					System.out.println(r.getStatus().toString());
					SendEmailError[] err =   r.getErrors();
					if(err != null) {
						for(SendEmailError e: err) {
							System.out.println("err is "+e.getErrorMessage());
						}
					}
						
				}
		} catch (MailjetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("can not send email", e);
		}
		
	}

}
