package com.v2.competency.management.service;

import com.v2.competency.management.dtos.RolePlayUserPromptRequest;
import com.v2.competency.management.dtos.RolePlayUserPromptResponse;
import com.v2.competency.management.entities.RolePlayUserPromptMapping;

public interface RolePlayUserPromptMappingService {
	
	RolePlayUserPromptMapping saveOrUpdate(
            RolePlayUserPromptRequest request);

    RolePlayUserPromptResponse getByTestNameAndCompanyId(
            String testName, String companyId);

}
