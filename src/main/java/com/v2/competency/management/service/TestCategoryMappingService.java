package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.TestCategoryMapping;

public interface TestCategoryMappingService {
	
	public TestCategoryMapping findByPrimaryKey( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory, @Param("subSubCategory") String subSubCategory,
			@Param("testIdentifier") String testIdentifier);
	
	
	public List<TestCategoryMapping> findTestsBySubSubCategory( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory, @Param("subSubCategory") String subSubCategory
				);
	
	public List<TestCategoryMapping> findTestsBySubCategory( @Param("companyId") String companyId, @Param("category") String category, @Param("subCategory") String subCategory);
	
	
	public List<TestCategoryMapping> findTestsByCategory( @Param("companyId") String companyId, @Param("category") String category);
	
	public List<String> findDistinctCategories(@Param("companyId") String companyId);
	
	public List<String> findSubCategories(@Param("companyId") String companyId,  @Param("category") String category);
	
	public List<String> findSubSubCategories(@Param("companyId") String companyId,  @Param("category") String category, @Param("subCategory") String subCategory);
	
	public TestCategoryMapping saveOrUpdate(TestCategoryMapping mapping);

}
