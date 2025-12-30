package com.v2.competency.management.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Tenant;

public interface TenantRepo extends CrudRepository<Tenant, Long> {
	
	@Query("select u from Tenant u where u.orgName =:orgName and u.companyId =:companyId and u.tenantAdminEmail =:tenantAdminEmail")
	public Tenant findByIdentity( @Param("orgName") String orgName,@Param("companyId") String companyId, @Param("tenantAdminEmail") String tenantAdminEmail);
	
	
	@Query("select u from Tenant u where u.companyId =:companyId")
	public Tenant findTenantByCompanyId(@Param("companyId") String companyId);
	
}
