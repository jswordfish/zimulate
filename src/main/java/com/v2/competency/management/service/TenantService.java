package com.v2.competency.management.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.v2.competency.management.entities.Tenant;

public interface TenantService {
	
	
	public Tenant findByIdentity(String email, String companyId, String tenantAdminEmail);
	
	
	Tenant saveOrUpdate(Tenant tenant, MultipartFile image) throws IOException;
	
	public Boolean isCompanyIdExisting(String companyId);
	
	
	public List<Tenant> findAll();
	

	public Tenant findTenantByCompanyId(String companyId);
	
}
