package com.v2.competency.management.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.service.HelloWorldService;

@RestController
public class HelloWorldController {
	
	@Autowired
	HelloWorldService service;
	
	@GetMapping("/printHelloWorld")
	public String Display(String token) {
		
		return service.display();
		
	}

}
