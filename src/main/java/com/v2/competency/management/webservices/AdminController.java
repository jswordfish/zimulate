package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.dtos.Dropdown;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.OrgHierarchy;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.service.OrgHierarchyService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
public class AdminController {
	@Autowired
	OrgHierarchyService orgHierarchyService;
	
	@Autowired
	UserService userService;
	
	
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	PropertyConfig propertyConfig;
	
	
	
	
	
	@RequestMapping(value = "/all_levels", method = RequestMethod.GET)
//	@PreAuthorize("hasRole('ADMIN')")
	@CrossOrigin
	public ResponseEntity<?> getAllLevels(@RequestAttribute String user, @RequestAttribute String role, @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		 List<OrgHierarchy> list =  orgHierarchyService.getAllHierarchyLevels(companyId);
		 int count = 0;
		 String level = "Level ";
		 Map<String, List<OrgHierarchy>> map = new HashMap<>();
		 Tenant ten = tenantService.findTenantByCompanyId(companyId);
		 for(OrgHierarchy hierarchy : list) {
			hierarchy.setCompanyName(ten.getOrgName());
			 
			if(map.get(hierarchy.getLevel()) == null) {
				List<OrgHierarchy> l = new ArrayList<>();
				l.add(hierarchy);
				map.put(hierarchy.getLevel(), l);
			}
			else {
				List<OrgHierarchy> l =  map.get(hierarchy.getLevel());
				l.add(hierarchy);
			}
				
		 }
		 List<Dropdown> dropDowns = new ArrayList<>();
		 for(String key : map.keySet()) {
			 Dropdown dropdown = Dropdown.builder().level(key).list(map.get(key)).build();
			 dropDowns.add(dropdown);
		 }
		 
		 java.util.Collections.sort(dropDowns, (d1, d2) -> d1.getLevel().compareTo(d2.getLevel()));
		  return ResponseEntity.ok(dropDowns);
	}
	
	 @RequestMapping(value="uploadHierarchies",method=RequestMethod.POST)  
	 @CrossOrigin
	    public ResponseEntity<?> uploadHierarchies( @RequestParam MultipartFile file,  @RequestParam String companyId,
	           HttpSession session, @RequestParam String token) throws Exception{  
		 
		 try {
			 Tenant tenant = tenantService.findTenantByCompanyId(companyId);
			 if(tenant == null) {
				 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
			 }
			 
			List<OrgHierarchy> levels = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, OrgHierarchy.class);
				System.out.println("Printing List Data: " +levels);
				for(OrgHierarchy level : levels) {
					level.setCompanyId(level.getCompanyId().trim());
					level.setRoleOrDesig(level.getRoleOrDesig().trim());
					level.setCompanyName(level.getCompanyName() == null?tenant.getCompanyName(): level.getCompanyName().trim());
					level.setLevel(level.getLevel().trim());
					String parentRoleOrDesig = level.getParentRoleOrDesig();
						if(parentRoleOrDesig != null) {
							level.setParentRoleOrDesig(level.getParentRoleOrDesig().trim());
						}
					
					String parent = level.getParentRoleOrDesig();
					if(!companyId.equals(level.getCompanyId())) {
						throw new RuntimeException("Non Existent Company Id "+level.getCompanyId());
					}
					
//					if(!tenantService.isCompanyIdExisting(level.getCompamyId())) {
//						return ResponseEntity.badRequest().body("Non Existent Company Id in uploaded file"+level.getCompamyId());
//					}
					
					if(parent != null) {
						OrgHierarchy p =  orgHierarchyService.findByRoleOrDesig(parent, companyId);
						if(p == null) {
							//throw new RuntimeException("parent level should exist");
							return ResponseEntity.badRequest().body("parent level should exist "+parent);
						}
					}
						
						level.setCompanyId(companyId);
						orgHierarchyService.saveOrUpdate(level);
					
				}
				
				 return ResponseEntity.ok("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
		}
	 }
	 
	 @RequestMapping(value="uploadUsers",method=RequestMethod.POST)  
	 @CrossOrigin
	    public  ResponseEntity<?> uploadUsers( @RequestParam String companyId, @RequestParam MultipartFile file,  
	           HttpSession session, @RequestParam String token) throws Exception{  
		 
		 try {
			 
			 if(!tenantService.isCompanyIdExisting(companyId)) {
				 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
			 }
			 
			 List<User> users = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX,User.class);
				System.out.println("Printing List Data: " +users);
				for(User user : users) {
					if(!user.getCompanyId().equals(companyId)) {
						throw new RuntimeException("Invalid Company Id "+user.getCompanyId());
					}
					
//					if(!tenantService.isCompanyIdExisting(user.getCompamyId())) {
//						return ResponseEntity.badRequest().body("Non Existent Company Id in uploaded file"+user.getCompamyId());
//					}
					
					user.setCompanyId(companyId);
					userService.saveOrUpdate(user);
					
				}
				 return ResponseEntity.ok("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
		}
	 }
	 
	 @RequestMapping(value="users",method=RequestMethod.GET)  
	 @CrossOrigin
	    public ResponseEntity<?> fetchUsers(  @RequestParam String companyId,
	           HttpSession session, @RequestParam String token) throws Exception{  
		 List<User> users = userService.findUsersByCompanyId(companyId);
		 return ResponseEntity.ok(users);
	 }
	 
	 
	
	 
	 @RequestMapping(value="usersByPage",method=RequestMethod.GET)  
	 @CrossOrigin
	    public ResponseEntity<?> fetchUsersByPage( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
	           HttpSession session, @RequestParam String token) throws Exception{  
		 if(pageNumber == null) {
				pageNumber = 0;
			}
		 Page<User> users = userService.getUsers(companyId,  PageRequest.of(pageNumber, 15));
		
		 PaginatedResponseDto res = new PaginatedResponseDto();
		 res.setRecordsFrom(users.getNumber());
		 res.setRecordsTo(users.getNumberOfElements());
		 res.setTotalNumberOfPages(users.getTotalPages());
		 res.setSelectedPage(pageNumber + 1);
		// res.setPreviousPage(pageNumber  -1);
		 res.setList(users.getContent());
		 return ResponseEntity.ok(res);
	 }
	 
	 @RequestMapping(value="usersByRoleOrDesig",method=RequestMethod.GET)  
	 @CrossOrigin
	    public ResponseEntity<?> usersByRoleOrDesig(  
	           HttpSession session, @RequestParam String companyId,  @RequestParam String roleOrDesig, 
	           @RequestParam String token) throws Exception{  
		 List<User> users = userService.findUsersByRoleOrDesig(roleOrDesig, companyId);
		 return ResponseEntity.ok(users);
	 }
	 
	 @RequestMapping(value="searchUsers",method=RequestMethod.GET)  
	 @CrossOrigin
	    public ResponseEntity<?> searchUsers(@RequestParam String companyId,  
	           HttpSession session, @RequestParam String search, 
	           @RequestParam String token) throws Exception{  
		 List<User> users = userService.searchUsers(search, companyId);
		 return ResponseEntity.ok(users);
	 }
	 
	 
	 @RequestMapping(value="userByEmail",method=RequestMethod.GET)  
	 @CrossOrigin
	    public ResponseEntity<?> userByEmail(  
	           HttpSession session, @RequestParam String companyId,  @RequestParam String email, 
	           @RequestParam String token) throws Exception{  
		User user = userService.findByEmail(email, companyId);
		 return ResponseEntity.ok(user);
	 }

}
