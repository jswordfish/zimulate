package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Role;

public interface RoleRepo extends CrudRepository<Role, Long> {

	
	 public Role findByRoleNameAndCompanyId(String roleName, String companyId);
	 
	 @Query("select r from Role r where r.companyId=:companyId")
	 List<Role> findRolesForCompany(@Param("companyId")  String companyId);
	 
	 public Page<Role> findRolesByCompanyId(@Param("companyId") String companyId, Pageable pageable);

}


