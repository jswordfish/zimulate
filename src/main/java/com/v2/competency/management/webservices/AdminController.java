package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	 
//	 @RequestMapping(value="uploadUsers",method=RequestMethod.POST)  
//	 @CrossOrigin
//	    public  ResponseEntity<?> uploadUsers( @RequestParam String companyId, @RequestParam MultipartFile file,  
//	           HttpSession session, @RequestParam String token) throws Exception{  
//		 
//		 try {
//			 
//			 if(!tenantService.isCompanyIdExisting(companyId)) {
//				 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
//			 }
//			 
//			 List<User> users = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX,User.class);
//				System.out.println("Printing List Data: " +users);
//				for(User user : users) {
//					if(!user.getCompanyId().equals(companyId)) {
//						throw new RuntimeException("Invalid Company Id "+user.getCompanyId());
//					}
//					
////					if(!tenantService.isCompanyIdExisting(user.getCompamyId())) {
////						return ResponseEntity.badRequest().body("Non Existent Company Id in uploaded file"+user.getCompamyId());
////					}
//					
//					user.setCompanyId(companyId);
//					userService.saveOrUpdate(user);
//					
//				}
//				 return ResponseEntity.ok("ok");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
//		}
//	 }
	 
	 private String trim(String value) {
		    return value == null ? null : value.trim();
		}
	 
	 private String getExceptionMessage(Exception e) {

		    Throwable cause = e;

		    while (cause.getCause() != null) {
		        cause = cause.getCause();
		    }

		    if (cause.getMessage() != null && !cause.getMessage().trim().isEmpty()) {
		        return cause.getMessage();
		    }

		    return e.getClass().getSimpleName();
		}
	 
	 private Map<String, Object> createErrorResponse(
		        String code,
		        String message,
		        Object errors) {

		    Map<String, Object> response = new LinkedHashMap<>();

		    response.put("success", false);
		    response.put("code", code);
		    response.put("message", message);

		    if (errors != null) {
		        response.put("errors", errors);
		    }

		    return response;
		}
	 
	 private void normalizeUser(User user) {

		    if (user == null) {
		        return;
		    }

		    user.setFirstName(trim(user.getFirstName()));
		    user.setLastName(trim(user.getLastName()));
		    user.setRoleOrDesig(trim(user.getRoleOrDesig()));
		    user.setLocation(trim(user.getLocation()));
		    user.setDepartment(trim(user.getDepartment()));
		    user.setEmail(trim(user.getEmail()));
		    user.setEmpId(trim(user.getEmpId()));
		    user.setPassword(trim(user.getPassword()));
		    user.setReportTo(trim(user.getReportTo()));
		}
	 
	 private List<String> validateUser(User user, String companyId) {

		    List<String> errors = new ArrayList<>();

		    if (user == null) {
		        errors.add("User data is empty.");
		        return errors;
		    }

		    /*
		     * First Name
		     */
		    if (user.getFirstName() == null
		            || user.getFirstName().trim().isEmpty()) {

		        errors.add(
		                "First Name is required. Please enter the user's first name."
		        );
		    }

		    /*
		     * Last Name
		     */
		    if (user.getLastName() == null
		            || user.getLastName().trim().isEmpty()) {

		        errors.add(
		                "Last Name is required. Please enter the user's last name."
		        );
		    }

		    /*
		     * Role or Designation
		     */
		    if (user.getRoleOrDesig() == null
		            || user.getRoleOrDesig().trim().isEmpty()) {

		        errors.add(
		                "Role or Designation is required. "
		                        + "Please enter the user's role or designation."
		        );
		    }

		    /*
		     * Location
		     */
		    if (user.getLocation() == null
		            || user.getLocation().trim().isEmpty()) {

		        errors.add(
		                "Location is required. "
		                        + "Please enter the user's location."
		        );
		    }

		    /*
		     * Department
		     */
		    if (user.getDepartment() == null
		            || user.getDepartment().trim().isEmpty()) {

		        errors.add(
		                "Department is required. "
		                        + "Please enter the user's department."
		        );
		    }

		    /*
		     * Email
		     */
		    if (user.getEmail() == null
		            || user.getEmail().trim().isEmpty()) {

		        errors.add(
		                "Email is required. "
		                        + "Please enter a valid email address."
		        );

		    } else if (!isValidEmail(user.getEmail())) {

		        errors.add(
		                "Email '" + user.getEmail()
		                        + "' is not in a valid email format. "
		                        + "Please enter an email such as user@example.com."
		        );
		    }

		    /*
		     * Employee ID
		     */
		    if (user.getEmpId() == null
		            || user.getEmpId().trim().isEmpty()) {

		        errors.add(
		                "Emp Id is required. "
		                        + "Please enter the employee ID."
		        );
		    }

		    /*
		     * Password
		     */
		    if (user.getPassword() == null
		            || user.getPassword().trim().isEmpty()) {

		        errors.add(
		                "Password is required. "
		                        + "Please enter a password for the user."
		        );
		    }

		    /*
		     * Report To
		     */
		    if (user.getReportTo() == null
		            || user.getReportTo().trim().isEmpty()) {

		        errors.add(
		                "Report to is required. "
		                        + "Please enter the name or employee ID of the person "
		                        + "this user reports to."
		        );
		    }

		    /*
		     * Is Reviewer
		     *
		     * Boolean is slightly different from String.
		     * If Poiji cannot map the Excel value, it may become null.
		     */
		    if (user.getReviewer() == null) {

		        errors.add(
		                "Is Reviewer is required. "
		                        + "Please enter either TRUE or FALSE."
		        );
		    }

		    return errors;
		}
	 
	 private boolean isValidEmail(String email) {

		    return email != null
		            && email.matches(
		                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
		            );
		}
	 
	 @RequestMapping(value = "uploadUsers", method = RequestMethod.POST)
	 @CrossOrigin
	 public ResponseEntity<?> uploadUsers(
	         @RequestParam String companyId,
	         @RequestParam MultipartFile file,
	         HttpSession session,
	         @RequestParam String token) {

	     try {

	         // Validate companyId
	         if (companyId == null || companyId.trim().isEmpty()) {
	             return ResponseEntity.badRequest().body(
	                     createErrorResponse(
	                             "UPLOAD_VALIDATION_FAILED",
	                             "Company Id is required.",
	                             null
	                     )
	             );
	         }

	         companyId = companyId.trim();

	         if (!tenantService.isCompanyIdExisting(companyId)) {
	             return ResponseEntity.badRequest().body(
	                     createErrorResponse(
	                             "INVALID_COMPANY_ID",
	                             "The provided Company Id '" + companyId
	                                     + "' does not exist.",
	                             null
	                     )
	             );
	         }

	         // Validate file
	         if (file == null || file.isEmpty()) {
	             return ResponseEntity.badRequest().body(
	                     createErrorResponse(
	                             "FILE_REQUIRED",
	                             "Please upload a valid Excel file.",
	                             null
	                     )
	             );
	         }

	         List<User> users = Poiji.fromExcel(
	                 file.getInputStream(),
	                 PoijiExcelType.XLSX,
	                 User.class
	         );

	         if (users == null || users.isEmpty()) {
	             return ResponseEntity.badRequest().body(
	                     createErrorResponse(
	                             "EMPTY_FILE",
	                             "The uploaded Excel file does not contain any users.",
	                             null
	                     )
	             );
	         }

	         /*
	          * STEP 1:
	          * Normalize all values first.
	          */
	         for (User user : users) {
	             normalizeUser(user);
	         }

	         /*
	          * STEP 2:
	          * Validate the complete Excel file BEFORE saving anything.
	          */
	         List<Map<String, Object>> validationErrors = new ArrayList<>();

	         // Excel header is row 1, therefore first data row = row 2
	         for (int i = 0; i < users.size(); i++) {

	             User user = users.get(i);

	             int excelRowNumber = i + 2;

	             List<String> errors = validateUser(user, companyId);

	             if (!errors.isEmpty()) {

	                 Map<String, Object> rowError = new LinkedHashMap<>();

	                 rowError.put("rowNumber", excelRowNumber);
	                 rowError.put(
	                         "email",
	                         user.getEmail() != null ? user.getEmail() : ""
	                 );
	                 rowError.put("errors", errors);

	                 validationErrors.add(rowError);
	             }
	         }

	         /*
	          * If ANY row is invalid, don't import anything.
	          */
	         if (!validationErrors.isEmpty()) {

	             Map<String, Object> response = new LinkedHashMap<>();

	             response.put("success", false);
	             response.put("code", "UPLOAD_VALIDATION_FAILED");
	             response.put(
	                     "message",
	                     "The Excel file contains validation errors. "
	                             + "Please correct the highlighted rows and upload the file again."
	             );
	             response.put("errors", validationErrors);

	             return ResponseEntity.badRequest().body(response);
	         }

	         /*
	          * STEP 3:
	          * All rows are valid, so start importing.
	          */
	         List<Map<String, Object>> importErrors = new ArrayList<>();

	         for (int i = 0; i < users.size(); i++) {

	             User user = users.get(i);

	             int excelRowNumber = i + 2;

	             try {

	                 user.setCompanyId(companyId);

	                 userService.saveOrUpdate(user);

	             } catch (Exception e) {

	                 Map<String, Object> rowError = new LinkedHashMap<>();

	                 rowError.put("rowNumber", excelRowNumber);
	                 rowError.put(
	                         "email",
	                         user.getEmail() != null ? user.getEmail() : ""
	                 );
	                 rowError.put(
	                         "message",
	                         "Unable to import this user."
	                 );
	                 rowError.put(
	                         "details",
	                         getExceptionMessage(e)
	                 );

	                 importErrors.add(rowError);
	             }
	         }

	         /*
	          * STEP 4:
	          * Handle database/import failures.
	          */
	         if (!importErrors.isEmpty()) {

	             Map<String, Object> response = new LinkedHashMap<>();

	             response.put("success", false);
	             response.put("code", "USER_IMPORT_FAILED");
	             response.put(
	                     "message",
	                     "Some users could not be imported. "
	                             + "Please review the errors and try again."
	             );
	             response.put("errors", importErrors);

	             return ResponseEntity.badRequest().body(response);
	         }

	         /*
	          * Everything succeeded.
	          */
	         Map<String, Object> response = new LinkedHashMap<>();

	         response.put("success", true);
	         response.put("code", "USER_IMPORT_SUCCESS");
	         response.put(
	                 "message",
	                 users.size() + " user(s) imported successfully."
	         );
	         response.put("totalUsers", users.size());

	         return ResponseEntity.ok(response);

	     } catch (IOException e) {

	         Map<String, Object> response = new LinkedHashMap<>();

	         response.put("success", false);
	         response.put("code", "INVALID_EXCEL_FILE");
	         response.put(
	                 "message",
	                 "The uploaded Excel file could not be read. "
	                         + "Please make sure it is a valid .xlsx file."
	         );
	         response.put("details", e.getMessage());

	         return ResponseEntity.badRequest().body(response);

	     } catch (Exception e) {

	         Map<String, Object> response = new LinkedHashMap<>();

	         response.put("success", false);
	         response.put("code", "USER_IMPORT_FAILED");
	         response.put(
	                 "message",
	                 "An unexpected error occurred while importing users."
	         );
	         response.put("details", getExceptionMessage(e));

	         return ResponseEntity.badRequest().body(response);
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
