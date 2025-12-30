package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.repos.CompetencyRepo;
import com.v2.competency.management.service.CompetencyService;
@Service
@Transactional
public class CompetencyServiceImpl implements CompetencyService{
	
	@Autowired
	CompetencyRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public List<Competency> findByLevelAndCompanyId(String level, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByLevelAndCompanyId(level, companyId);
	}

	@Override
	public Competency findByCompetency(String competency, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByCompetency(competency, companyId);
	}

	@Override
	public List<Competency> findByParentCompetency(String competency, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByParentCompetency(competency, companyId);
	}
	
	private void validate(Competency competency) {
		if(competency.getCompetency() == null || competency.getCompetency().trim().length() == 0) {
			throw new RuntimeException(" Competency name can not be blank");
		}
		
		if(competency.getCompanyId() == null || competency.getCompanyId().trim().length() == 0) {
			throw new RuntimeException(" Company Id can not be blank");
		}
		
		if(competency.getParentCompetency() != null && competency.getParentCompetency().trim().length() > 0) {
			Competency parent = 	repo.findByCompetency(competency.getParentCompetency(), competency.getCompanyId());
			if(parent == null) {
				throw new RuntimeException("Parent Competency can not be null");
			}
		}
		
	}

	@Override
	public Competency saveOrUpdate(Competency competency) {
		// TODO Auto-generated method stub
		validate(competency);
		Competency competency2 = null;
			if(competency.getParentCompetency() == null || competency.getParentCompetency().trim().length() == 0 ) {
				competency2 = findByCompetency(competency.getCompetency(), competency.getCompanyId());
			}
			else {
				competency2 = findByCompetencyAndParentCompetency(competency.getCompetency(), competency.getParentCompetency(), competency.getCompanyId());
			}
		if(competency2 == null) {
			competency.setCreateDate(new Date());
			return repo.save(competency);
		}
		else {
			competency.setId(competency2.getId());
			competency.setCreateDate(competency2.getCreateDate());
			competency.setUpdateDate(new Date());
			mapper.map(competency, competency2);
			return repo.save(competency2);
		}
	}

	@Override
	public Page<Competency> getCompetenciesByCompanyId(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.getCompetenciesByCompanyId(companyId, pageable);
	}

	@Override
	public Page<Competency> getCompetenciesByLevelAndCompanyId(String level, String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.getCompetenciesByLevelAndCompanyId(level, companyId, pageable);
	}

	@Override
	public Competency findByCompetencyAndParentCompetency(String competency, String parentCompetency,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findByCompetencyAndParentCompetency(competency, parentCompetency, companyId);
	}

}
