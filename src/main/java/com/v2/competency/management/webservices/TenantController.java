package com.v2.competency.management.webservices;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.UserService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TenantController {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	S3Client s3Client;
	
	private final S3Presigner s3Presigner;
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(
	        value = "createTenant",
	        method = RequestMethod.POST,
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<?> createTenant(
	        @RequestPart("tenantJson") String tenantJson,
	        @RequestPart(value = "image", required = false) MultipartFile image,
	        HttpSession session,
	        @RequestParam String token
	) throws Exception {

	    ObjectMapper objectMapper = new ObjectMapper();

	    Tenant tenant = objectMapper.readValue(
	            tenantJson,
	            Tenant.class
	    );

//	    if (tenantService.isCompanyIdExisting(tenant.getCompanyId())) {
//	        return ResponseEntity.badRequest()
//	                .body("Company Id already exists!!");
//	    }

	    Objects.requireNonNull(tenant);
	    Objects.requireNonNull(tenant.getOrgName());
	    Objects.requireNonNull(tenant.getCompanyId());
	    Objects.requireNonNull(tenant.getTenantAdminEmail());
	    Objects.requireNonNull(tenant.getPassword());

	    tenantService.saveOrUpdate(tenant, image);

	    User user = User.builder()
	            .email(tenant.getTenantAdminEmail())
	            .password(tenant.getPassword())
	            .department("COMPANY ADMIN")
	            .build();

	    user.setCompanyId(tenant.getCompanyId());
	    user.setCompanyName(
	            tenant.getOrgName() == null
	                    ? tenant.getCompanyName()
	                    : tenant.getOrgName()
	    );
	    user.setFirstName("Company");
	    user.setLastName("Admin");
	    user.setRoleOrDesig("Company Admin");

	    userService.saveOrUpdateCompanyAdminUser(user);

	    return ResponseEntity.ok("ok");
	}
	
	@GetMapping("/getTenantImageUrl")
	public ResponseEntity<String> getImageUrl(
			@RequestParam String companyId,
			@RequestParam String token){
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
		if(tenant==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company Id invalid : "+companyId);
		}
		
		String keyName = tenant.getS3KeyName();
		
		PresignedGetObjectRequest presignedRequest =
	            s3Presigner.presignGetObject(p -> p
	                    .getObjectRequest(
	                            GetObjectRequest.builder()
	                                    .bucket("letsworkcentres")
	                                    .key(keyName)
	                                    .build()
	                    )
	                    .signatureDuration(Duration.ofDays(7))
	            );

	    return ResponseEntity.ok(
	            presignedRequest.url().toString()
	    );
		
	}
	
	@GetMapping("/s3/presigned-url")
	public ResponseEntity<String> getPresignedUrl(
	        @RequestParam String s3Key,
	        @RequestParam String token
	) {

	    PresignedGetObjectRequest presignedRequest =
	            s3Presigner.presignGetObject(p -> p
	                    .getObjectRequest(
	                            GetObjectRequest.builder()
	                                    .bucket("letsworkcentres")
	                                    .key(s3Key)
	                                    .build()
	                    )
	                    .signatureDuration(Duration.ofDays(7))
	            );

	    return ResponseEntity.ok(
	            presignedRequest.url().toString()
	    );
	}
	
	
	@RequestMapping(value="fetchTenants",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchTenants( 
           HttpSession session) throws Exception{  
		
		List<Tenant> tenants = tenantService.findAll();
		 return ResponseEntity.ok(tenants);
	 }
	
	
}
