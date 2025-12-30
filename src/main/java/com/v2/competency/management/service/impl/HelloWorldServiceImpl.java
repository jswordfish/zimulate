package com.v2.competency.management.service.impl;

import org.springframework.stereotype.Service;

import com.v2.competency.management.service.HelloWorldService;

@Service
public class HelloWorldServiceImpl implements HelloWorldService{

	@Override
	public String display() {
		// TODO Auto-generated method stub
		return "Hello World !!!!!!!!";
	}

}
