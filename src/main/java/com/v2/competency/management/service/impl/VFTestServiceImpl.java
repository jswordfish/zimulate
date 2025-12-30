package com.v2.competency.management.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.entities.Question_Source;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.repos.VFTestRepo;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.VFTestService;
@Service
public class VFTestServiceImpl implements VFTestService{
	@Autowired
	VFTestRepo repo;

	XmlMapper xmlMapper = new XmlMapper();
	
	@Autowired
	TenantService tenantService;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	@Override
	public VFTest findByTestIdentifier(String testIdentifier, String companyId) {
		return repo.findByTestIdentifier(testIdentifier, companyId);
	}

	@Override
	public VFTest saveOrUpdate(VFTest test) {
		
		if(test.getCompanyId() == null || test.getCompanyId().trim().length() == 0) {
			throw new RuntimeException("Company Id can not be blank");
		}
		if(test.getQuestionSource() ==null) {
			throw new RuntimeException("Question Source can not be blank or null");
		}
			
			
		if( test.getQuestionSource().equalsIgnoreCase(Question_Source.AI.getSource()) && test.getTestXml() == null) {
			throw new RuntimeException("xml can not be blank or null");
		}
		
		if( !test.getQuestionSource().equalsIgnoreCase(Question_Source.AI.getSource()) && test.getTestXmlForKB() == null) {
			throw new RuntimeException("xml can not be blank or null");
		}
		
		if(tenantService.findTenantByCompanyId(test.getCompanyId()) == null) {
			throw new RuntimeException("Company "+test.getCompanyId()+" does not exist");
		}
		
		try {
			if(test.getQuestionSource().equals(Question_Source.AI.getSource())) {
				CompetencyTest t = xmlMapper.readValue(test.getTestXml().getBytes(), CompetencyTest.class);
			}
			if(!test.getQuestionSource().equals(Question_Source.AI.getSource())) {
				CompetencyTest t = xmlMapper.readValue(test.getTestXmlForKB().getBytes(), CompetencyTest.class);
			}
			
		}
		catch(Exception e) {
			throw new RuntimeException("invalid xml");
		}
		
		VFTest test2 = findByTestIdentifier(test.getTestIdentifier(), test.getCompanyId());
		if(test2 == null) {
			test.setCreateDate(new Date());
			return repo.save(test);
		}
		else {
			test.setId(test2.getId());
			test.setCreateDate(test2.getCreateDate());
			test.setUpdateDate(new Date());
			mapper.map(test, test2);
			return repo.save(test2);
		}
				
		
	}

	@Override
	public Page<VFTest> findTestsByCompanyId(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findTestsByCompanyId(companyId, pageable);
	}

	@Override
	public Page<VFTest> findTestsContainingIdentifierText(String search, String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findTestsContainingIdentifierText(search, companyId, pageable);
	}

	@Override
	public VFTest findByTestName(String testName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByTestName(testName, companyId);
	}

	@Override
	public Page<VFTest> findTestsContainingTestNameText(String search, String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findTestsContainingTestNameText(search, companyId, pageable);
	}

}
