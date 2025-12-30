package com.v2.competency.management.webservices;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.v2.competency.management.common.util.TokenService2;
import com.v2.competency.management.dtos.AuthenticationResponse;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.repos.UserRepo;
import com.v2.competency.management.service.UserService;


@RestController
@CrossOrigin
public class AuthenticationController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepo repo;
	
	TokenService2 tokenService = new TokenService2();
	
	@CrossOrigin
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody User user, @RequestParam(required = false) String superFlag)
			throws Exception {

		Objects.requireNonNull(user);
		Objects.requireNonNull(user.getEmail());
		Objects.requireNonNull(user.getPassword());
		Objects.requireNonNull(user.getCompanyId());
		User u =  userService.findByEmail(user.getEmail(), user.getCompanyId());
		
		if(superFlag != null && superFlag.equals("yes")) {
			if(!user.getEmail().equals("admin@itechseed.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin User");
			}
		}
		else {
			if(user.getEmail().equals("admin@itechseed.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin login Page");
			}
		}
		
			if(u == null) {
				if(user.getEmail().equals("admin@itechseed.com") && user.getPassword().equals("12345")) {
					String token =  tokenService.generateToken("SUPER_ADMIN", user.getEmail());
					return ResponseEntity.ok(token);
				}
				
				return ResponseEntity.ok("failure");
			}
			else {
				//System.out.println("passed pwd "+user.getPassword()+" db pwd "+u.getPassword());
					if(user.getPassword().equals(u.getPassword())) {
						
			            String role;

			            if (Boolean.TRUE.equals(u.getIsOperations())) {
			                
			                role = "OPERATIONAL";
			            } 
			            
			            else if (u.getOrgHierarchy() != null && u.getOrgHierarchy().getRoleOrDesig() != null) {
			                role = u.getOrgHierarchy().getRoleOrDesig();
			            } 
			            
			            else if (repo.isManager(u.getEmail())) {
			                role = "MANAGER";
			            } 
			            
			            else {
			                role = "USER";
			            }
			            

			            
			            String token = tokenService.generateToken(role, u.getEmail());
			            return ResponseEntity.ok(token);
			            
			        } else {
			            return ResponseEntity.ok("failure : Wrong PAssword");
			        }
				
				
			}
		
	} 
	
	@CrossOrigin
	@RequestMapping(value = "/token2", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken2(@RequestBody User user, @RequestParam(required = false) String superFlag)
			throws Exception {

		Objects.requireNonNull(user);
		Objects.requireNonNull(user.getEmail());
		Objects.requireNonNull(user.getPassword());
		Objects.requireNonNull(user.getCompanyId());
		User u =  userService.findByEmail(user.getEmail(), user.getCompanyId());
		AuthenticationResponse response = new AuthenticationResponse();
		
		if(superFlag != null && superFlag.equals("yes")) {
			if(!user.getEmail().equals("admin@itechseed.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin User");
			}
		}
		else {
			if(user.getEmail().equals("admin@itechseed.com")) {
				return ResponseEntity.ok("failure : Not a Super Admin login Page");
			}
		}
		
			if(u == null) {
				if(user.getEmail().equals("admin@itechseed.com") && user.getPassword().equals("12345")) {
					String token =  tokenService.generateToken("SUPER_ADMIN", user.getEmail());
					response.setToken(token);
					return ResponseEntity.ok(response);
				}
				
				return ResponseEntity.ok("failure");
			}
			else {
				//System.out.println("passed pwd "+user.getPassword()+" db pwd "+u.getPassword());
					if(user.getPassword().equals(u.getPassword())) {
						
						
						
						String token =  tokenService.generateToken(u.getOrgHierarchy().getRoleOrDesig(), u.getEmail());
						response.setToken(token);
						return ResponseEntity.ok(response);
					}
					else {
						return ResponseEntity.ok("failure : Wrong PAssword");
					}
				
				
			}
		
	} 

}
