package com.v2.competency.management.service;

import org.springframework.web.multipart.MultipartFile;

public interface PersonaImageService {
	
	String uploadPersonaImage(
            String ageRange,
            String nationality,
            Boolean male,
            String setting,
            MultipartFile image
    ) throws Exception;

    String getRandomPersonaImage(
            String ageRange,
            String nationality,
            Boolean male,
            String setting
    );

}
