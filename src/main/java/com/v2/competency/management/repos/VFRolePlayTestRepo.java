package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v2.competency.management.entities.VFRolePlayTest;

@Repository
public interface VFRolePlayTestRepo extends JpaRepository<VFRolePlayTest, Long>  {
	
//	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId and v.testName =:testName and v.competency =:competency and v.parentCompetency =:parentCompetency and v.questionText =:questionText")
//	public VFRolePlayTest findUniqueRecord( String testName, String competency, String parentCompetency, String questionText, String companyId);
	
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId and v.testName =:testName")
	public VFRolePlayTest findUniqueRecord( String testName,  String companyId);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId  and v.competency =:competency and v.parentCompetency =:parentCompetency")
	public List<VFRolePlayTest> findRolePlayTestsForCompetency(String competency, String parentCompetency, String companyId);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId")
	 public Page<VFRolePlayTest> getRolePlayTestsByCompanyId(@Param("companyId") String companyId, Pageable pageable);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId  and v.testName =:testName")
	 VFRolePlayTest findRolePlayTestsByTestName(String companyId, String testName);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId  and v.published is not null and v.published=true")
	 public List<VFRolePlayTest> findPublishedTests(String companyId);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId  and v.published is not null and v.published=true AND ( LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'sales', '%')) OR LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'train', '%'))    )")
	 public Page<VFRolePlayTest> searchTrainingRolePlays(@Param("companyId") String companyId,  Pageable pageable);
	 
	 @Query("select v from VFRolePlayTest v where  v.companyId =:companyId  and v.published is not null and v.published=true AND ( LOWER(v.aiPersona)  LIKE LOWER(CONCAT('%', 'evaluator', '%')) OR LOWER(v.aiPersona)  LIKE LOWER(CONCAT('%', 'customer', '%')) )  ")
	 public Page<VFRolePlayTest> searchAssessmentRolePlays(@Param("companyId") String companyId,  Pageable pageable);
	 
	 Page<VFRolePlayTest> findAll(Pageable pageable);
	 
	 @Query("SELECT v FROM VFRolePlayTest v " +
		       "WHERE LOWER(v.industry) IN :industries " +
		       "AND v.companyId = :companyId")
		Page<VFRolePlayTest> findByCompanyIdAndIndustriesIgnoreCase(@Param("companyId") String companyId,
		                                                            @Param("industries") List<String> industries,
		                                                            Pageable pageable);
	 
	 @Query("SELECT v FROM VFRolePlayTest v " +
		       "WHERE v.companyId = :companyId " +
		       "AND (:search IS NULL OR " +
		       "     LOWER(v.testName) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "     OR LOWER(v.industry) LIKE LOWER(CONCAT('%', :search, '%'))" +
		       ") " +
		       "AND ((:isIndustriesEmpty = true) OR LOWER(v.industry) IN :industries)")
		Page<VFRolePlayTest> findByCompanyIdAndFilters(
		        @Param("companyId") String companyId,
		        @Param("industries") List<String> industries,
		        @Param("isIndustriesEmpty") boolean isIndustriesEmpty,
		        @Param("search") String search,
		        Pageable pageable);
	 
	 @Query("SELECT DISTINCT v.industry FROM VFRolePlayTest v WHERE v.industry IS NOT NULL AND v.companyId = :companyId")
	    List<String> findDistinctIndustriesByCompanyId(@Param("companyId") String companyId);
	 
	 @Query("SELECT v FROM VFRolePlayTest v " +
		       "WHERE v.companyId = :companyId " +
		       "AND v.published IS NOT NULL " +
		       "AND v.published = true " +
		       "AND (LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'sales', '%')) " +
		       "OR LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'train', '%'))) " +
		       "AND (:search IS NULL OR TRIM(:search) = '' " +
		       "OR LOWER(v.testName) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.questionText) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.productInfo) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.competitionInfo) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.publicTestLink) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.competency) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.parentCompetency) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.industry) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.defaultQuestionPrompt) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.rolePlayLabelForUI) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.agentId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.knowledgebaseId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.reportVersion) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.userPersona) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.rolePlayType) LIKE LOWER(CONCAT('%', :search, '%')))")
		Page<VFRolePlayTest> searchTrainingRolePlays(
		        @Param("companyId") String companyId,
		        @Param("search") String search,
		        Pageable pageable);
	 
	 @Query("SELECT v FROM VFRolePlayTest v " +
		       "WHERE v.companyId = :companyId " +
		       "AND v.published IS NOT NULL " +
		       "AND v.published = true " +
		       "AND (LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'evaluator', '%')) " +
		       "OR LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', 'customer', '%'))) " +
		       "AND (:search IS NULL OR TRIM(:search) = '' " +
		       "OR LOWER(v.testName) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.questionText) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.productInfo) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.competitionInfo) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.publicTestLink) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.competency) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.parentCompetency) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.industry) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.defaultQuestionPrompt) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.rolePlayLabelForUI) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.agentId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.knowledgebaseId) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.reportVersion) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.userPersona) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.aiPersona) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(v.rolePlayType) LIKE LOWER(CONCAT('%', :search, '%')))")
		Page<VFRolePlayTest> searchAssessmentRolePlays(
		        @Param("companyId") String companyId,
		        @Param("search") String search,
		        Pageable pageable);
	 

}
