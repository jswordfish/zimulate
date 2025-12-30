package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.Tenant;

public interface TenantService {
	
	
	public Tenant findByIdentity(String email, String companyId, String tenantAdminEmail);
	
	
	public Tenant saveOrUpdate(Tenant tenant);
	
	public Boolean isCompanyIdExisting(String companyId);
	
	
	public List<Tenant> findAll();
	

	public Tenant findTenantByCompanyId(String companyId);
	
}
