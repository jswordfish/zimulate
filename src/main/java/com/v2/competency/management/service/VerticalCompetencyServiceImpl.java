package com.v2.competency.management.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.VerticalCompetency;
import com.v2.competency.management.repos.VerticalRepo;

@Service
@Transactional
public class VerticalCompetencyServiceImpl implements VerticalCompetencyService{

	@Autowired
	VerticalRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	@Autowired
	CompetencyService competencyService;
	
	@Override
	public List<String> findVerticalsForCompanyId(String companyId) {
		
		return repo.findVerticalsForCompanyId(companyId);
	}

	@Override
	public VerticalCompetency findByCompetency(String vertical, String competency, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByCompetency(vertical, competency, companyId);
	}

	@Override
	public List<VerticalCompetency> findCompetenciesForVertical(String vertical, String companyId) {
		// TODO Auto-generated method stub
		return repo.findCompetenciesForVertical(vertical, companyId);
	}

	@Override
	public VerticalCompetency saveOrUpdate(VerticalCompetency verticalCompetency) {
		Objects.requireNonNull(verticalCompetency.getCompanyId());
		Objects.requireNonNull(verticalCompetency.getVertical());
		Objects.requireNonNull(verticalCompetency.getCompetency());
		if(competencyService.findByCompetency(verticalCompetency.getCompetency(), verticalCompetency.getCompanyId()) == null) {
			throw new RuntimeException("Competency "+verticalCompetency.getCompetency()+" does not exist for "+verticalCompetency.getVertical());
		}
		
		VerticalCompetency verticalCompetency2 = findByCompetency(verticalCompetency.getVertical(), verticalCompetency.getCompetency(), verticalCompetency.getCompanyId());
			if(verticalCompetency2 == null) {
				verticalCompetency.setCreateDate(new Date());
				return repo.save(verticalCompetency);
			}
			else {
				verticalCompetency.setCreateDate(verticalCompetency2.getCreateDate());
				verticalCompetency.setUpdateDate(new Date());
				verticalCompetency.setId(verticalCompetency2.getId());
				mapper.map(verticalCompetency, verticalCompetency2);
				return repo.save(verticalCompetency2);
			}
		
	}

}
