package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.Category;
import com.v2.competency.management.repos.CategoryRepo;
import com.v2.competency.management.service.CategoryService;
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	CategoryRepo categoryRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public Category findUniquerRecord(String category, String subCategory, String competency, String parentCompetency,
			String assessmentName, String companyId) {
		return categoryRepo.findUniquerRecord(category, subCategory, competency, parentCompetency, assessmentName, companyId);
	}

	@Override
	public List<String> findDistictCategories(String companyId) {
		return categoryRepo.findDistictCategories(companyId);
	}

	@Override
	public List<Category> findSubCategories(String companyId, String category) {
		return categoryRepo.findSubCategories(companyId, category);
	}

	@Override
	public Category saveOrUpdate(Category category) {
		Category category2 = findUniquerRecord(category.getCategory(), category.getSubCategory(), category.getCompetency(), category.getParentCompetency(), category.getAssessmentName(), category.getCompanyId());
		if(category2 == null) {
			category.setCreateDate(new Date());
			return categoryRepo.save(category);
		}
		else {
			category.setId(category2.getId());
			category.setCreateDate(category2.getCreateDate());
			category.setUpdateDate(new Date());
			mapper.map(category, category2);
			return categoryRepo.save(category2);
		}
	}

}
