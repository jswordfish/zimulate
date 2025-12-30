package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.entities.Role;

public interface RoleService {
	
	public Role findByRoleNameAndCompanyId(String roleName, String companyId);
	 
	 List<Role> findRolesForCompany( String companyId);
	 
	 public Page<Role> findRolesByCompanyId(String companyId, Pageable pageable);
	 
	 
	 public Role saveOrUpdate(Role role);

}
