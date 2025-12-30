package com.v2.competency.management;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class TestEmail {
	String sender = "sales@zimulate.me";
	
	Map<String, String> variables = new HashMap<>();
	
	@org.junit.jupiter.api.Test
	public void testMailJetApi() {
		ClientOptions options = ClientOptions.builder()
                .apiKey("8259d51f87852f8c7b9f6b08e627f94d")
                .apiSecretKey("184fc8e67edae36b2b5ad191e8bd2e53")
                .build();

        MailjetClient client = new MailjetClient(options);
        String msgId = ""+System.currentTimeMillis();
        String[] tos = {"jatin.sutaria@thev2technologies.com"};
        List<SendContact> toList = new ArrayList<>();
        for(String t : tos) {
        	System.out.println("sending mail to  "+t);
        	toList.add(new SendContact(t));
        }
        
        variables.put("result_url", "https://zimulate.me");
        variables.put("firstname", "Anil");
        variables.put("name", "Sell a Car");
        
        TransactionalEmail message1 = TransactionalEmail
                .builder()
                .to(toList)
                .from(new SendContact(sender, "Operations, Zimulate"))
                .subject("Anil, Your Pitch Score for 'Sell a Car' Roleplay")
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
		}
	}

}
