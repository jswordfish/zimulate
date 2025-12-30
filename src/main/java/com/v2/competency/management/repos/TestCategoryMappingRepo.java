package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.TestCategoryMapping;

public interface TestCategoryMappingRepo  extends CrudRepository<TestCategoryMapping, Long> {
	

	
	@Query("select t from TestCategoryMapping t where t.companyId =:companyId and t.category =:category and t.subCategory =:subCategory and t.subSubCategory =:subSubCategory and  t.testIdentifier =:testIdentifier")
	public TestCategoryMapping findByPrimaryKey( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory, @Param("subSubCategory") String subSubCategory,
			@Param("testIdentifier") String testIdentifier);
	
	
	@Query("select t from TestCategoryMapping t where t.companyId =:companyId and t.category =:category and t.subCategory =:subCategory and t.subSubCategory =:subSubCategory")
	public List<TestCategoryMapping> findTestsBySubSubCategory( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory, @Param("subSubCategory") String subSubCategory
				);
	
	@Query("select t from TestCategoryMapping t where t.companyId =:companyId and t.category =:category and t.subCategory =:subCategory")
	public List<TestCategoryMapping> findTestsBySubCategory( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory);
	
	
	@Query("select t from TestCategoryMapping t where t.companyId =:companyId and t.category =:category")
	public List<TestCategoryMapping> findTestsByCategory( @Param("companyId") String companyId, @Param("category") String category);
	
	@Query("select DISTINCT(t.category) from TestCategoryMapping t where t.companyId =:companyId ")
	public List<String> findDistinctCategories(@Param("companyId") String companyId);
	
	@Query("select DISTINCT(t.subCategory) from TestCategoryMapping t where t.companyId =:companyId and t.category =:category")
	public List<String> findSubCategories(@Param("companyId") String companyId,  @Param("category") String category);
	
	@Query("select DISTINCT(t.subSubCategory) from TestCategoryMapping t where t.companyId =:companyId and t.category =:category  and t.subCategory =:subCategory")
	public List<String> findSubSubCategories(@Param("companyId") String companyId,  @Param("category") String category, @Param("subCategory") String subCategory);
	
	
}
