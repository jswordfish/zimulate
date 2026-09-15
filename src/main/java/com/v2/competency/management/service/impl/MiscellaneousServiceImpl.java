package com.v2.competency.management.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v2.competency.management.service.MiscellaneousService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class MiscellaneousServiceImpl implements MiscellaneousService{
	
	@Autowired
	PropertyConfig config;
	
	Map<String, String> customPropertiesMap = new HashMap<>();
	
	private static final String DELIMITER = "=";
	
	@Override
	public String getValue(String key) {
		
		System.out.println("custom properties path : " + config.getCustomPropertiesPath().toString());
		
		if(customPropertiesMap.size() == 0) {
			try {
				List<String> lines = FileUtils.readLines(new File(config.getCustomPropertiesPath()), StandardCharsets.UTF_8);
				
				for (String line : lines) {
				    if (line == null || line.trim().isEmpty() || line.startsWith("#")) {
				        continue;
				    }
				    
				    String[] parts = line.split(DELIMITER, 2);
				    if (parts.length == 2) {
				    	customPropertiesMap.put(parts[0].trim(), parts[1].trim());
				    }
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		
		return customPropertiesMap.get(key);
	}

}
