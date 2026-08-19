package com.v2.competency.management.webservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.SortParam;
import com.v2.competency.management.dtos.UserWithTestDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.UserService;
import com.v2.competency.management.service.VFRolePlayTestSessionService;

@RestController
@CrossOrigin
public class UserController {
	
	
	ObjectMapper mapper = new ObjectMapper();
	
	static String VALIDATION_OK = "ok";
	
	@Autowired
	UserService service;
	
	@Autowired
	VFRolePlayTestSessionService testSessionService;
	
	@Autowired
	AssessmentMapperService assessmentMapperService;
	
	
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-DD hh:mm:ss");
	
	@GetMapping("/search by name paginated")
	public ResponseEntity<PaginatedResponseDto> searchUsers(
	        @RequestParam String search,
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page) {

	    PaginatedResponseDto response = service.searchUsersPaginated(search, companyId, page);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/manager-employees")
    public ResponseEntity<PaginatedResponseDto> getEmployeesByManager(
            @RequestParam String managerEmail,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {

        PaginatedResponseDto response = service.findEmployeesByManager(managerEmail, page);
        return ResponseEntity.ok(response);
    }
	
	// 1. Find sessions by testIdentifier
    @GetMapping("/find sessions of a particular test")
    public ResponseEntity<Page<VFRolePlayTestSession>> getSessionsByTest(
            @RequestParam String testIdentifier,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<VFRolePlayTestSession> sessions =
                testSessionService.findUserSessionsForTest(testIdentifier, companyId, pageable);

        return ResponseEntity.ok(sessions);
    }

    // 2. Find sessions by email
    @GetMapping("/find test sessions of a particular user filtered by test name and persona")
    public ResponseEntity<Page<VFRolePlayTestSession>> getSessionsByEmailAndTestNameAndPersona(
            @RequestParam String email,
            @RequestParam String companyId,
            @RequestParam(required = false) String testName,
            @RequestParam(required = false) String personas, 
            @RequestParam(required = false) String difficultyLevel, 
            @RequestParam(required = false) String sort,    
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token) {

        // Parse persona list
        List<String> personaList = null;
        boolean isPersonasEmpty = true;
        if (personas != null && !personas.trim().isEmpty()) {
            personaList = Arrays.stream(personas.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            isPersonasEmpty = false;
        }

        // Parse sort string
        Sort sortObj = Sort.by("createDate").descending(); // default
        if (sort != null && !sort.trim().isEmpty()) {
            List<Sort.Order> orders = Arrays.stream(sort.split(","))
                    .map(String::trim)
                    .map(s -> {
                        String[] parts = s.split("=");
                        if (parts.length == 2) {
                            String field = parts[0].trim();
                            String direction = parts[1].trim().toLowerCase();
                            return direction.equals("desc")
                                    ? Sort.Order.desc(field)
                                    : Sort.Order.asc(field);
                        } else {
                            return Sort.Order.asc(s); // fallback
                        }
                    })
                    .collect(Collectors.toList());
            sortObj = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<VFRolePlayTestSession> sessions = testSessionService
                .findUserSessions(email, companyId, testName, personaList, isPersonasEmpty, difficultyLevel, pageable);

        return ResponseEntity.ok(sessions);
    }
    
    
    @GetMapping("/List users that are assigned a particular test")
    public ResponseEntity<Page<UserWithTestDto>> getUsersByTest(
            @RequestParam String testName,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {

        Page<UserWithTestDto> response = assessmentMapperService.getUsersByTestName(testName, page);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(
	        @RequestParam String email,
	        @RequestParam String companyId,
	        @RequestParam String token
	) {

	    service.sendForgotPasswordEmail(
	            email,
	            companyId
	    );

	    return ResponseEntity.ok(
	            "Password reset link has been sent to your email."
	    );
	}
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword,
            @RequestParam String token
    ) {

        service.resetPassword(
                userId,
                newPassword
        );

        return ResponseEntity.ok(
                "Password has been reset successfully."
        );
    }
    
    @PostMapping("/search users general")
    public ResponseEntity<PaginatedResponseDto> searchUsersGeneral(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String empId,
            @RequestParam(required = false) String managerEmail,
            @RequestParam(required = false) Boolean external,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page
    ) {
        PaginatedResponseDto response = service.searchUsersGeneral(
                name, email, location, empId, managerEmail, external, page
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search users final")
    public ResponseEntity<PaginatedResponseDto> searchUsers(
            @RequestParam String companyId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String managerEmail,
            @RequestParam(required = false) Boolean external,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token) {

        Page<User> usersPage = service.searchUsersFinal(search, managerEmail, external, companyId, page, size);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * size) + 1);
        response.setRecordsTo((page * size) + usersPage.getNumberOfElements());
        response.setTotalNumberOfRecords((int) usersPage.getTotalElements());
        response.setTotalNumberOfPages(usersPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(usersPage.getContent());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/get manager of an employee")
    public User getManagerByEmployee(
    		@RequestParam String email,
    		@RequestParam String companyId,
    		@RequestParam String token){
    	
    	return service.getManagerByEmployee(email, companyId);
    	
    }
    
    @PostMapping("/assign-manager-to-users")
    public String assignManagerToUsers(
            @RequestParam String userEmails, 
            @RequestParam String companyId,
            @RequestParam String managerEmail,
            @RequestParam String token) {

        List<String> emailList = Arrays.stream(userEmails.split(","))
                .map(String::trim)          
                .filter(e -> !e.isEmpty())  
                .collect(Collectors.toList()); 

        return service.assignManagerToUsers(emailList, companyId, managerEmail);
    }
    
    @GetMapping("/List Role Play Tests assigned to a user")
    public ResponseEntity<PaginatedResponseDto> getUserRolePlayTests(
            @RequestParam String companyId,
            @RequestParam String email,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String token) {

        PaginatedResponseDto response = assessmentMapperService
                .findAssessmentsForUserWithFilters(companyId, email, search, sort, page, size);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/List Role Play Tests assigned to a user sorted by parameters")
    public ResponseEntity<PaginatedResponseDto> getUserRolePlayTests(
            @RequestParam String companyId,
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody(required = false) List<SortParam> sortParams,
            @RequestParam String token) {

        List<Sort.Order> orders = new ArrayList<>();
        if (sortParams != null) {
            for (SortParam param : sortParams) {
                Sort.Direction direction = "DESC".equalsIgnoreCase(param.getSortType())
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, param.getSortParam()));
            }
        }

        Pageable pageable = orders.isEmpty() 
            ? PageRequest.of(page, size) 
            : PageRequest.of(page, size, Sort.by(orders));

        Page<AssessmentMapper> resultPage = assessmentMapperService.findAssessmentsForUser(companyId, email, pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(page * size + 1);
        response.setRecordsTo(page * size + resultPage.getNumberOfElements());
        response.setTotalNumberOfRecords((int) resultPage.getTotalElements());
        response.setTotalNumberOfPages(resultPage.getTotalPages());
        response.setSelectedPage(page);
        response.setList(resultPage.getContent());

        return ResponseEntity.ok(response);
    }

}
