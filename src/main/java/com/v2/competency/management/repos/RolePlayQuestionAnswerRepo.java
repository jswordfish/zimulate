package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v2.competency.management.entities.RolePlayQuestionAnswer;


public interface RolePlayQuestionAnswerRepo extends CrudRepository<RolePlayQuestionAnswer, Long> {
	
	@Query("select v from RolePlayQuestionAnswer v where testName =:testName  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt order by v.id")
	public List<RolePlayQuestionAnswer> findAllQAForUser(@Param("testName") String testName, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	

	@Query("select v.question from RolePlayQuestionAnswer v where testName =:testName  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt order by v.id")
	public List<String> findAllQuesTextForUser(@Param("testName") String testName, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);
	
	
	@Query("select v.answer from RolePlayQuestionAnswer v where testName =:testName  and v.email =:email and v.companyId =:companyId and v.attempt =:attempt order by v.id")
	public List<String> getAllAnswersForRolePlayTestForUser(@Param("testName") String testName, @Param("email") String email, @Param("companyId") String companyId, @Param("attempt") Integer attempt);


	

}
