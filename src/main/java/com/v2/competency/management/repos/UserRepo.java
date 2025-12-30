package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.User;

public interface UserRepo extends JpaRepository<User, Long> {
	
	@Query("select u from User u where u.email =:email and u.companyId =:companyId")
	public User findByEmail( @Param("email") String email,@Param("companyId") String companyId);
	
	
	@Query("select u from User u where u.empId =:empId and u.companyId =:companyId")
	public User findByEmpId(@Param("empId") String empId,@Param("companyId") String companyId);
	
	
	@Query(value = "select u from User u join u.orgHierarchy o where o.roleOrDesig=:roleOrDesig and o.companyId =:companyId and (u.external is null or u.external =false)")
	 public List<User> findUsersByRoleOrDesig( @Param("roleOrDesig") String roleOrDesig, @Param("companyId") String companyId);
	
	
	//@Query(value="SELECT  u FROM User u WHERE ( u.companyId =:companyId) and ( (lower(u.firstName) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.lastName) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.location) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.email) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.empId) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.department) LIKE lower(CONCAT('%',:search,'%')) ) ")	
	// 
	@Query(value="SELECT  u FROM User u WHERE ( u.companyId =:companyId) and  ( lower(u.firstName) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.lastName) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.location) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.email) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.empId) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.department) LIKE lower(CONCAT('%',:search,'%')) )  and (u.external is null or u.external =false)")
	public List<User> searchUsers(@Param("search") String search, @Param("companyId") String companyId);
	
	@Query("SELECT u FROM User u " +
		       "WHERE u.companyId = :companyId " +
		       "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "  OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
		       "AND (u.external IS NULL OR u.external = false)")
	Page<User> searchUsersPaginated(@Param("search") String search,
            @Param("companyId") String companyId,
            Pageable pageable);
	
	@Query("SELECT u FROM User u " +
	           "WHERE u.reportTo = :managerEmail " +
	           "AND (u.external IS NULL OR u.external = false)")
	    Page<User> findEmployeesByManager(@Param("managerEmail") String managerEmail,
	                                      Pageable pageable);
	
	
	@Query(value="SELECT l FROM User l WHERE l.companyId=:companyId  and (l.external is null  or l.external =false)")
	public Page<User> getUsers(@Param("companyId") String companyId, Pageable pageable);
	
	
	@Query(value="SELECT l FROM User l WHERE l.companyId=:companyId   and (l.external is null or l.external =false)")
	 public List<User> findUsersByCompanyId( @Param("companyId") String companyId);
	

	@Query("SELECT u " +
		       "FROM User u " +
		       "WHERE ( " +
		       "   (:name IS NOT NULL AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
		       "                          OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))) " +
		       "   OR (:email IS NOT NULL AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
		       "   OR (:location IS NOT NULL AND LOWER(u.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
		       "   OR (:empId IS NOT NULL AND LOWER(u.empId) LIKE LOWER(CONCAT('%', :empId, '%'))) " +
		       ") " +
		       "AND (:managerEmail IS NULL OR LOWER(u.reportTo) = LOWER(:managerEmail)) " +
		       "AND (:external IS NULL OR u.external = :external)")
	    Page<User> searchUsersGeneral(
	        @Param("name") String name,
	        @Param("email") String email,
	        @Param("location") String location,
	        @Param("empId") String empId,
	        @Param("managerEmail") String managerEmail,
	        @Param("external") Boolean external,
	        Pageable pageable
	    );
	
	@Query("SELECT u " +
		       "FROM User u " +
		       "WHERE u.companyId = :companyId " + // always enforce companyId
		       "AND (:search IS NULL OR " +
		       "      LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(u.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(u.empId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       ") " +
		       "AND (:managerEmail IS NULL OR LOWER(u.reportTo) = LOWER(:managerEmail)) " +
		       "AND (:external IS NULL OR u.external = :external)")
		Page<User> searchUsersFinal(@Param("search") String search,
		                            @Param("managerEmail") String managerEmail,
		                            @Param("external") Boolean external,
		                            @Param("companyId") String companyId,
		                            Pageable pageable);
	
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.reportTo = :email")
	boolean isManager(@Param("email") String email);
}
