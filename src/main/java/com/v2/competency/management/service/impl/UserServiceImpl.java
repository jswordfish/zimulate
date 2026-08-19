package com.v2.competency.management.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.OrgHierarchy;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.repos.UserRepo;
import com.v2.competency.management.service.MailClientService;
import com.v2.competency.management.service.OrgHierarchyService;
import com.v2.competency.management.service.UserService;
@Service
@Transactional
public class UserServiceImpl implements UserService{
	
	private static final int PAGE_SIZE = 10;
	
	@Autowired
	UserRepo repo;
	
	@Autowired
	OrgHierarchyService orgHierarchyService;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	String companyAdminRole = "Company Admin";
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	MailClientService mailClientService;
	
	
	OrgHierarchy createCompanyAdminRole(){
		OrgHierarchy hierarchy = new OrgHierarchy("Level 1000", companyAdminRole, null);
		return orgHierarchyService.saveOrUpdate(hierarchy);
	}

	@Override
	public User findByEmail(String email, String companyId) {
		// TODO Auto-generated method stub
		try {
			return repo.findByEmail(email, companyId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(email +"   "+companyId);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public User findByEmpId(String empId, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByEmpId(empId, companyId);
	}

	@Override
	public synchronized User saveOrUpdate(User user) {
		// TODO Auto-generated method stub
		
		if(!(user.getExternal() != null && user.getExternal())) {
			if(user.getOrgHierarchy() != null) {
				user.setRoleOrDesig(user.getOrgHierarchy().getRoleOrDesig());
			}
			
			if(user.getRoleOrDesig() == null || user.getRoleOrDesig().trim().length() == 0) {
				//System.out.println("2      email is "+user.getEmail()+" u.getexternal "+user.getExternal()+" role "+(user.getOrgHierarchy()==null?"null":user.getOrgHierarchy().getRoleOrDesig()));
				throw new RuntimeException("Role or designation can not be null");
			}
			
			OrgHierarchy orgHierarchy = orgHierarchyService.findByRoleOrDesig(user.getRoleOrDesig(), user.getCompanyId());
			if(orgHierarchy == null) {
				throw new RuntimeException("Role or designation invalid");
			}
			user.setOrgHierarchy(orgHierarchy);
		}
		
		User parentUser = repo.findByEmail(user.getReportTo(), user.getCompanyId());
//		if(parentUser==null) {
//			throw new RuntimeException("The user you are reporting to does not exists");
//		}
		
		User user2 = null;
			if(user.getEmail() != null && user.getEmail().trim().length() > 0) {
				user2 = findByEmail(user.getEmail(), user.getCompanyId());
			}
			else if(user.getEmpId() != null && user.getEmpId().trim().length() > 0){
				user2 = findByEmpId(user.getEmail(), user.getCompanyId());
			}
			else {
				throw new RuntimeException("No role or Emp Id present");
			}
		
			if(user2 == null) {
				
				user.setCreateDate(new Date());
				user = repo.save(user);
				
//				if(config.getSendEmail() != null && config.getSendEmail()) {
//					List<String> to = Arrays.asList(user.getEmail());
//					Map<String, String> map = new HashMap<>();
//					String first = user.getFirstName() == null?"User":user.getFirstName();
//					String last = user.getLastName()==null?"":user.getLastName();
//					String link = config.getPlatformLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//					String linkAdmin = config.getPlatformAdminLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//					map.put("fullName",first+" "+last);
//					map.put("email",user.getEmail());
//					map.put("password", user.getPassword());
////						if(user.getRoleOrDesig().equalsIgnoreCase("Company Admin")) {
////							map.put("platformLink", linkAdmin);
////						}
////						else {
////							map.put("platformLink", link);
////						}
//					map.put("platformLink", link);
//
//					
//					mailClientService.sendMail(to, null, 5471471l, map, "Onboarding you on BSRF!!!");
//				}
//				
				return user;
			}
			else {
				user.setId(user2.getId());
				user.setCreateDate(user2.getCreateDate());
				user.setUpdateDate(new Date());
				mapper.map(user, user2);
			}
		
		return repo.save(user2);
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		
		List<User> users = new ArrayList<>();
		Iterable<User> ire = repo.findAll();
		ire.forEach(u -> users.add(u));
		return users;
	}

	@Override
	public List<User> findUsersByRoleOrDesig(String roleOrDesig, String companyId) {
		// TODO Auto-generated method stub
		return repo.findUsersByRoleOrDesig(roleOrDesig, companyId);
	}

	@Override
	public List<User> searchUsers(String search, String companyId) {
		// TODO Auto-generated method stub
		return repo.searchUsers(search, companyId);
	}
	
	@Override
	public PaginatedResponseDto searchUsersPaginated(String search, String companyId, int page) {
	    int size = 10; // fixed page size
	    Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
	    Page<User> userPage = repo.searchUsersPaginated(search, companyId, pageable);

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setRecordsFrom(page * size + 1);
	    dto.setRecordsTo(page * size + userPage.getNumberOfElements());
	    dto.setTotalNumberOfRecords((int) userPage.getTotalElements());
	    dto.setTotalNumberOfPages(userPage.getTotalPages());
	    dto.setSelectedPage(page + 1);
	    dto.setList(userPage.getContent());

	    return dto;
	}

	@Override
	public Page<User> getUsers(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.getUsers(companyId, pageable);
	}

	@Override
	public User saveOrUpdateCompanyAdminUser(User user) {
		// TODO Auto-generated method stub
				if(user.getRoleOrDesig() == null || user.getRoleOrDesig().trim().length() == 0) {
					throw new RuntimeException("Role or designation can not be null");
				}
				
				OrgHierarchy orgHierarchy = orgHierarchyService.findByRoleOrDesig(companyAdminRole, user.getCompanyId());
				if(orgHierarchy == null) {
					orgHierarchy = createCompanyAdminRole();
				}
				user.setOrgHierarchy(orgHierarchy);
				User user2 = null;
					if(user.getEmail() != null && user.getEmail().trim().length() > 0) {
						user2 = findByEmail(user.getEmail(), user.getCompanyId());
					}
					else if(user.getEmpId() != null && user.getEmpId().trim().length() > 0){
						user2 = findByEmpId(user.getEmail(), user.getCompanyId());
					}
					else {
						throw new RuntimeException("No role or Emp Id present");
					}
				
					if(user2 == null) {
						user.setCreateDate(new Date());
						user = repo.save(user);
//						System.out.println(" config email send "+config.getSendEmail());
//						if(config.getSendEmail() != null && config.getSendEmail()) {
//							List<String> to = Arrays.asList(user.getEmail());
//							Map<String, String> map = new HashMap<>();
//							String first = user.getFirstName() == null?"User":user.getFirstName();
//							String last = user.getLastName()==null?"":user.getLastName();
//							String link = config.getPlatformLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//							//String linkAdmin = config.getPlatformAdminLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//							map.put("fullName",first+" "+last);
//							map.put("email",user.getEmail());
//							map.put("password", user.getPassword());
//							map.put("platformLink", link);
//							
//							mailClientService.sendMail(to, null, 5471471l, map, "Onboarding you as BSRF Company Admin!!");
//							System.out.println(" tenant admin creation "+config.getSendEmail()+" mail sent");
//						}
						return user;
					}
					else {
						user.setId(user2.getId());
						user.setCreateDate(user2.getCreateDate());
						user.setUpdateDate(new Date());
						mapper.map(user, user2);
					}
				
				return repo.save(user2);
	}
	
	@Override
    public PaginatedResponseDto findEmployeesByManager(String managerEmail, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("firstName").ascending());
        Page<User> userPage = repo.findEmployeesByManager(managerEmail, pageable);

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom(page * PAGE_SIZE + 1);
        dto.setRecordsTo(page * PAGE_SIZE + userPage.getNumberOfElements());
        dto.setTotalNumberOfRecords((int) userPage.getTotalElements());
        dto.setTotalNumberOfPages(userPage.getTotalPages());
        dto.setSelectedPage(page + 1);
        dto.setList(userPage.getContent());

        return dto;
    }

	@Override
	public List<User> findUsersByCompanyId(String companyId) {
		// TODO Auto-generated method stub
		return repo.findUsersByCompanyId(companyId);
	}
	
	@Override
    public PaginatedResponseDto searchUsersGeneral(
            String name,
            String email,
            String location,
            String empId,
            String managerEmail,
            Boolean external,
            int page
    ) {
        // Business rule: if managerEmail is present AND external = true → error
        if (managerEmail != null && external != null && external) {
            throw new IllegalArgumentException("A user under a manager cannot be external.");
        }

        Pageable pageable = PageRequest.of(page, 10); // fixed size 10
        Page<User> users = repo.searchUsersGeneral(
                name, email, location, empId, managerEmail, external, pageable
        );

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((int) (pageable.getOffset() + 1));
        response.setRecordsTo((int) (pageable.getOffset() + users.getNumberOfElements()));
        response.setTotalNumberOfRecords((int) users.getTotalElements());
        response.setTotalNumberOfPages(users.getTotalPages());
        response.setSelectedPage(page);
        response.setList(users.getContent());

        return response;
    }
	
	@Override
	public Page<User> searchUsersFinal(String search, String managerEmail, Boolean external, String companyId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
	    return repo.searchUsersFinal(search, managerEmail, external, companyId, pageable);
	}

	@Override
	public User getManagerByEmployee(String email, String companyId) {
		// TODO Auto-generated method stub
		
		User user = repo.findByEmail(email, companyId);
		
		if(user==null) {
			throw new RuntimeException("User does not exists");
		}
		
		String managerEmail = user.getReportTo();
		
		User managerUser = repo.findByEmail(managerEmail, companyId);
		
		return managerUser;
		
	}

	
	@Override
	public String assignManagerToUsers(List<String> userEmails, String companyId, String managerEmail) {
	    User managerUser = repo.findByEmail(managerEmail, companyId);

	    if (managerUser == null) {
	        throw new RuntimeException("Manager does not exist");
	    }

	    List<String> updatedUsers = new ArrayList<>();
	    for (String userEmail : userEmails) {
	        User user = repo.findByEmail(userEmail, companyId);

	        if (user == null) {
	        	throw new RuntimeException("User does not exists");
	        }

	        user.setReportTo(managerEmail);
	        repo.save(user);
	        updatedUsers.add(user.getFirstName());
	    }

	    if (updatedUsers.isEmpty()) {
	        return "No users were updated because none were found";
	    }

	    return "Manager: " + managerUser.getFirstName() + " is assigned to users: " + String.join(", ", updatedUsers);
	}

	@Override
	public Long findUserIdByEmail(String email, String companyId) {
		// TODO Auto-generated method stub
		User user = repo.findByEmail(email, companyId);
		if(user==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist with the email : "+email);
		}
		return user.getId();
	}

	@Override
	public void sendForgotPasswordEmail(String email, String companyId) {

	    User user = repo.findByEmail(email, companyId);

	    if (user == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "User does not exist with the email : " + email
	        );
	    }

	    Long userId = user.getId();

	    String link = "https://zimulate.me/forgotPassword" + "?userId=" + userId;

	    ClientOptions options = ClientOptions.builder()
	            .apiKey("8259d51f87852f8c7b9f6b08e627f94d")
	            .apiSecretKey("184fc8e67edae36b2b5ad191e8bd2e53")
	            .build();

	    MailjetClient client = new MailjetClient(options);

	    Map<String, String> variables = new HashMap<>();
	    variables.put("link", link);

	    TransactionalEmail emailMessage = TransactionalEmail.builder()
	            .to(List.of(new SendContact(email)))
	            
	            
	            .from(new SendContact("sales@zimulate.me", "Zimulate"))
	            .subject("Reset Your Password")
	            .templateID(8276239L)
	            .templateLanguage(true)
	            .variables(variables)
	            .build();

	    SendEmailsRequest request = SendEmailsRequest.builder()
	            .message(emailMessage)
	            .build();

	    try {

	        request.sendWith(client);

	    } catch (MailjetException e) {

	        throw new RuntimeException(
	                "Failed to send forgot password email",
	                e
	        );
	    }
	}

	@Override
	@Transactional
	public void resetPassword(Long userId, String newPassword) {

	    User user = repo.findById(userId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.BAD_REQUEST,
	                    "User does not exist."
	            ));

	    user.setPassword(newPassword);

	    repo.save(user);
	}

}
