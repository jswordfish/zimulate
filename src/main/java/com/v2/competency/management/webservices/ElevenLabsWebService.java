package com.v2.competency.management.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.service.AgentService;

@RestController
public class ElevenLabsWebService {
	
	@Autowired
	AgentService elevenLabsAgentService;
	
	
//	@RequestMapping(value="createOrUpdateAgent",method=RequestMethod.POST)  
//    public ResponseEntity<?> uploadHierarchies( @RequestParam MultipartFile file,  @RequestParam String companyId,
//           HttpSession session, @RequestParam String token) throws Exception{  
//		
//	}
	

}
