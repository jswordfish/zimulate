package com.v2.competency.management.webservices;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.UserService;

@RestController
@CrossOrigin
public class TenantController {
	
	@Autowired
	TenantService tenantService;
	
	
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value="createTenant",method=RequestMethod.POST)  
    public ResponseEntity<?> createTenant( @RequestBody Tenant tenant,  
           HttpSession session, @RequestParam String token) throws Exception{  
		
		if(tenantService.isCompanyIdExisting(tenant.getCompanyId())) {
			return ResponseEntity.badRequest().body("Company Id already exists!!");
		}
		
		Objects.requireNonNull(tenant);
		Objects.requireNonNull(tenant.getOrgName());
		Objects.requireNonNull(tenant.getCompanyId());
		Objects.requireNonNull(tenant.getTenantAdminEmail());
		Objects.requireNonNull(tenant.getPassword());
		tenantService.saveOrUpdate(tenant);
		
		User user = User.builder().email(tenant.getTenantAdminEmail()).password(tenant.getPassword()).department("COMPANY ADMIN").build();
		user.setCompanyId(tenant.getCompanyId());
		user.setCompanyName(tenant.getOrgName() == null?tenant.getCompanyName():tenant.getOrgName());
		user.setFirstName("Company");
		user.setLastName("Admin");
		user.setRoleOrDesig("Company Admin");
		userService.saveOrUpdateCompanyAdminUser(user);
		
		 return ResponseEntity.ok("ok");
	 }
	
	
	@RequestMapping(value="fetchTenants",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchTenants( 
           HttpSession session) throws Exception{  
		
		List<Tenant> tenants = tenantService.findAll();
		 return ResponseEntity.ok(tenants);
	 }
	
	
}
