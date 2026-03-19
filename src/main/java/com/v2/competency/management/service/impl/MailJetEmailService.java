package com.v2.competency.management.service.impl;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.v2.competency.management.service.EmailService;
@Service
public class MailJetEmailService implements EmailService{
	
	String sender = "sales@zimulate.me";
	
	String apiKey = "8259d51f87852f8c7b9f6b08e627f94d";
	
	String secretKey = "184fc8e67edae36b2b5ad191e8bd2e53";
	
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
	
	@Autowired
	PropertyConfig config;

	@Override
	public void sendEmail(String email, String[] cc, String subject, String roleplay, Integer attempt, String firstName,
			String lastName, String persona, String companyId) {
		ClientOptions options = ClientOptions.builder()
                .apiKey("8259d51f87852f8c7b9f6b08e627f94d")
                .apiSecretKey("184fc8e67edae36b2b5ad191e8bd2e53")
                .build();

        MailjetClient client = new MailjetClient(options);
        String msgId = ""+System.currentTimeMillis();
        String[] tos = {email};
        List<SendContact> toList = new ArrayList<>();
        for(String t : tos) {
        	System.out.println("sending mail to  "+t);
        	toList.add(new SendContact(t));
        }
        
        List<SendContact> ccList = new ArrayList<>();
        for(String c : cc) {
        	System.out.println("sending mail to  "+c);
        	ccList.add(new SendContact(c));
        }
        
    	Map<String, String> variables = new HashMap<>();
    	String resultPage = config.getRolePlayResultBaseUrl()+companyId+"&email="+URLEncoder.encode(email)+"&roleplay="+URLEncoder.encode(roleplay)+"&attempt="+attempt
    			+"&firstName="+URLEncoder.encode(firstName)+"&lastName="+URLEncoder.encode(lastName);
    			
        variables.put("result_url", resultPage);
        variables.put("firstname", firstName);
        variables.put("roleplay", roleplay);
        variables.put("attempt", ""+attempt);
        variables.put("time", dateFormat.format(new Date()));
        variables.put("persona", persona);
        
        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(toList)
                .cc(ccList)
                .from(new SendContact(sender, "Operations, Zimulate"))
                .subject(firstName+", Your Pitch Insights!!!")
                .trackOpens(TrackOpens.ENABLED)
                .templateID(7222484l).templateLanguage(true)
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
					System.out.println("email sending status "+ r.getStatus().toString());
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
		}
		
	}

	@Override
	public void sendEmailWithtoIgnore(String email, String[] cc, String subject, String roleplay, Integer attempt,
			String firstName, String lastName, String persona, String companyId) {
		ClientOptions options = ClientOptions.builder()
                .apiKey("8259d51f87852f8c7b9f6b08e627f94d")
                .apiSecretKey("184fc8e67edae36b2b5ad191e8bd2e53")
                .build();

        MailjetClient client = new MailjetClient(options);
        String msgId = ""+System.currentTimeMillis();
        String[] tos = {"vinxent890@gmail.com"};
        List<SendContact> toList = new ArrayList<>();
        for(String t : tos) {
        	System.out.println("sending mail to  "+t);
        	toList.add(new SendContact(t));
        }
        
        List<SendContact> ccList = new ArrayList<>();
        for(String c : cc) {
        	System.out.println("sending mail to  "+c);
        	ccList.add(new SendContact(c));
        }
        
    	Map<String, String> variables = new HashMap<>();
    	String resultPage = config.getRolePlayResultBaseUrl()+companyId+"&email="+URLEncoder.encode(email)+"&roleplay="+URLEncoder.encode(roleplay)+"&attempt="+attempt
    			+"&firstName="+URLEncoder.encode(firstName)+"&lastName="+URLEncoder.encode(lastName);
    			
        variables.put("result_url", resultPage);
        variables.put("firstname", firstName);
        variables.put("roleplay", roleplay);
        variables.put("attempt", ""+attempt);
        variables.put("time", dateFormat.format(new Date()));
        variables.put("persona", persona);
        
        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(toList)
                .cc(ccList)
                .from(new SendContact(sender, "Operations, Zimulate"))
                .subject(firstName+", Your Pitch Insights!!!")
                .trackOpens(TrackOpens.ENABLED)
                .templateID(7222484l).templateLanguage(true)
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
					System.out.println("email sending status "+ r.getStatus().toString());
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
		}
	}

}
