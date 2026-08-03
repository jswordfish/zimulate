package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.v2.competency.management.entities.RolePlayPersonaMapping;

public interface RolePlayPersonaMappingRepo extends CrudRepository<RolePlayPersonaMapping, Long> {

	
	 
	 @Query("SELECT r FROM RolePlayPersonaMapping r WHERE r.type =:type AND r.companyId =:companyId")
	 public List<RolePlayPersonaMapping> findPersonasForTypeAndCompanyId(String type, String companyId);
	 
	 
	 
}