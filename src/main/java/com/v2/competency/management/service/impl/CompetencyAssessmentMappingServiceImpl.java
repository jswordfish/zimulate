package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.Proficiency;
import com.v2.competency.management.entities.CompetencyAssessmentMapping;
import com.v2.competency.management.repos.CompetencyAssessmentMappingRepo;
import com.v2.competency.management.service.CompetencyAssessmentMappingService;
import com.v2.competency.management.service.CompetencyService;
@Service
@Transactional
public class CompetencyAssessmentMappingServiceImpl implements CompetencyAssessmentMappingService{
	
	@Autowired
	CompetencyAssessmentMappingRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	@Autowired
	CompetencyService competencyService;

	@Override
	public CompetencyAssessmentMapping findByBusinessKey(String competency, String parentCompetency, String testName,
			String companyId) {
		return repo.findByBusinessKey(competency, parentCompetency, testName, companyId);
	}

	@Override
	public List<CompetencyAssessmentMapping> findAssessmentsForCompetency(String competency, String parentCompetency,
			String companyId) {
		return repo.findAssessmentsForCompetency(competency, parentCompetency, companyId);
	}

	@Override
	public CompetencyAssessmentMapping saveOrUpdate(CompetencyAssessmentMapping mapping) {
		Objects.requireNonNull(mapping.getCompanyId());
		Objects.requireNonNull(mapping.getCompetency());
		Objects.requireNonNull(mapping.getTestName());
		
		String competency = mapping.getCompetency();
		//String parentCompetency = mapping.getParentCompetency();
			if(competency.contains("###")) {
				competency = competency.substring(competency.indexOf("###")+3, competency.length());
			}
//			if(parentCompetency.contains("###")) {
//				parentCompetency = parentCompetency.substring(parentCompetency.indexOf("###")+3, parentCompetency.length());
//			}
		
		if(competencyService.findByCompetency(competency, mapping.getCompanyId()) == null) {
			throw new RuntimeException("Competency "+mapping.getCompetency()+" does not exist");
		}
		
		try {
			Proficiency p =  Proficiency.valueOf(mapping.getProficiencyLevel());
				if(p == null) {
					throw new RuntimeException("Invalid Proficiency Level "+mapping.getProficiencyLevel());
				}
			
		}
		catch(Exception e) {
			throw new RuntimeException("Invalid Proficiency Level "+mapping.getProficiencyLevel());
		}
		
		//CompetencyAssessmentMapping mapping2 = findByBusinessKeyWithoutTestName(mapping.getCompetency(), mapping.getParentCompetency(), mapping.getCompanyId());
		CompetencyAssessmentMapping mapping2 = findByBusinessKey(mapping.getCompetency(), mapping.getParentCompetency(), mapping.getTestName(), mapping.getCompanyId());
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

	@Override
	public CompetencyAssessmentMapping findByBusinessKeyWithoutTestName(String competency, String parentCompetency,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findByBusinessKeyWithoutTestName(competency, parentCompetency, companyId);
	}

	@Override
	public List<CompetencyAssessmentMapping> findConsolidatedAssessmentForCompetenciesByGroupName(
			String consolidatedAssessmentGroupName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findConsolidatedAssessmentForCompetenciesByGroupName(consolidatedAssessmentGroupName, companyId);
	}

	@Override
	public Page<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompany(String companyId,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findAllConsolidatedAssessmentByCompany(companyId, pageable);
	}

	@Override
	public List<CompetencyAssessmentMapping> findAllConsolidatedAssessmentByCompanyNoPagination(String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllConsolidatedAssessmentByCompanyNoPagination(companyId);
	}

}
