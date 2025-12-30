package com.v2.competency.management.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Category;

public interface CategoryService {
	
	
	public Category findUniquerRecord(@Param("category")  String category, @Param("subCategory")  String subCategory, @Param("competency")  String competency,
			 @Param("parentCompetency")  String parentCompetency, @Param("assessmentName")  String assessmentName,
			 @Param("companyId")  String companyId);
	 
	 public List<String> findDistictCategories(@Param("companyId")  String companyId);
	 
	 public List<Category> findSubCategories(@Param("companyId")  String companyId, @Param("category")  String category);

	 public Category saveOrUpdate(Category category);
}
