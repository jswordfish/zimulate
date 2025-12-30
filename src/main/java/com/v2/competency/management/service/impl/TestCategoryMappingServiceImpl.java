package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.TestCategoryMapping;
import com.v2.competency.management.repos.TestCategoryMappingRepo;
import com.v2.competency.management.service.TestCategoryMappingService;

@Service
@Transactional
public class TestCategoryMappingServiceImpl implements TestCategoryMappingService{
	@Autowired
	TestCategoryMappingRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public TestCategoryMapping findByPrimaryKey(String companyId, String category, String subCategory,
			String subSubCategory, String testIdentifier) {
		return repo.findByPrimaryKey(companyId, category, subCategory, subSubCategory, testIdentifier);
	}

	@Override
	public List<TestCategoryMapping> findTestsBySubSubCategory(String companyId, String category, String subCategory,
			String subSubCategory) {
		return repo.findTestsBySubSubCategory(companyId, category, subCategory, subSubCategory);
	}

	@Override
	public List<TestCategoryMapping> findTestsBySubCategory(String companyId, String category, String subCategory) {
		return repo.findTestsBySubCategory(companyId, category, subCategory);
	}

	@Override
	public List<TestCategoryMapping> findTestsByCategory(String companyId, String category) {
		return repo.findTestsByCategory(companyId, category);
	}

	@Override
	public List<String> findDistinctCategories(String companyId) {
		return repo.findDistinctCategories(companyId);
	}

	@Override
	public List<String> findSubCategories(String companyId, String category) {
		return repo.findSubCategories(companyId, category);
	}

	@Override
	public List<String> findSubSubCategories(String companyId, String category, String subCategory) {
		return repo.findSubSubCategories(companyId, category, subCategory);
	}

	@Override
	public TestCategoryMapping saveOrUpdate(TestCategoryMapping mapping) {
		Objects.requireNonNull(mapping.getCompanyId());
		Objects.requireNonNull(mapping.getCategory());
		Objects.requireNonNull(mapping.getTestIdentifier());
		Objects.requireNonNull(mapping.getTestName());
		mapping.setSubCategory((mapping.getSubCategory()==null || mapping.getSubCategory().trim().length() == 0)?"NA":mapping.getSubCategory());
		mapping.setSubCategory((mapping.getSubSubCategory()==null || mapping.getSubSubCategory().trim().length()==0)?"NA":mapping.getSubSubCategory());
		
		TestCategoryMapping mapping2 = findByPrimaryKey(mapping.getCompanyId(), mapping.getCategory(), mapping.getSubCategory(), mapping.getSubSubCategory(), mapping.getTestIdentifier());
			if(mapping2 == null) {
				mapping.setCreateDate(new Date());
				return repo.save(mapping);
			}
			else {
				mapping.setCreateDate(mapping2.getCreateDate());
				mapping.setId(mapping2.getId());
				mapping.setUpdateDate(new Date());
				mapper.map(mapping, mapping2);
				return repo.save(mapping2);
			}
	}

}
