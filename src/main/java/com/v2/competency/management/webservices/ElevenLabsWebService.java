package com.v2.competency.management.webservices;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.elevanlabs.dtos.Root;
import com.v2.competency.management.service.AgentService;

@RestController
public class ElevenLabsWebService {
	
	String API_KEY = "8fe509f9aa5293da449c4aa8cd5c0a9c88868770326366cbc3daf490a4310b9b";
	
	String END_POINT = "https://api.elevenlabs.io/v1/convai/agents/create";
	
	
	@Autowired
	AgentService elevenLabsAgentService;
	
	
	
	@RequestMapping(value="createOrUpdateAgent",method=RequestMethod.POST)  
    public ResponseEntity<?> createOrUpdateAgent( @RequestBody Root agentRoot, @RequestParam(required = false) String agentId,
           HttpSession session, @RequestParam String token) {  
		try {
			return ResponseEntity.ok(elevenLabsAgentService.createOrUpdateAgent(agentRoot, agentId)); 
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	

}
