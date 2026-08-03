package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.RolePlayPersonaMapping;

public interface RolePlayPersonaMappingService {
	
	 
	 public List<RolePlayPersonaMapping> findPersonasForTypeAndCompanyId(String testIdentifier, String companyId);
	 
	 
	 public RolePlayPersonaMapping addRolePlayPersonaMapping(RolePlayPersonaMapping mapping);
	 
	 public RolePlayPersonaMapping updateRolePlayPersonaMapping(RolePlayPersonaMapping mapping);
	 

}
