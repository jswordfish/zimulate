package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.User;

public interface UserService {
	
	public User findByEmail(String email, String companyId);
	
	
	public User findByEmpId(String empId, String companyId);
	
	public User saveOrUpdate(User user);
	
	
	public User saveOrUpdateCompanyAdminUser(User user);
	
	public List<User> findAll();
	
	public List<User> findUsersByRoleOrDesig( String roleOrDesig, String companyId);
	
	public List<User> searchUsers(String search, String companyId);
	
	public PaginatedResponseDto searchUsersPaginated(String search, String companyId, int page);
	
	PaginatedResponseDto findEmployeesByManager(String managerEmail, int page);
	
	public Page<User> getUsers(String companyId, Pageable pageable);
	
	public List<User> findUsersByCompanyId(  String companyId);
	
	PaginatedResponseDto searchUsersGeneral(
	        String name,
	        String email,
	        String location,
	        String empId,
	        String managerEmail,
	        Boolean external,
	        int page
	    );
	
	public Long findUserIdByEmail(String email, String companyId);
	
	public Page<User> searchUsersFinal(String search, String managerEmail, Boolean external, String companyId, int page, int size);
	
	public User getManagerByEmployee(String email, String companyId);
	
	String assignManagerToUsers(List<String> userEmails, String companyId, String managerEmail);
	
	void sendForgotPasswordEmail(String email, String companyId);

    void resetPassword(Long userId, String newPassword);
	

}
