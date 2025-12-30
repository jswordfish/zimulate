package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.JobDescription;
import com.v2.competency.management.entities.Role;
import com.v2.competency.management.repos.JobDescRepo;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.JobDescriptionService;

@Service
@Transactional
public class JobDescriptionImpl implements JobDescriptionService{
	
	@Autowired
	JobDescRepo repo;
	
	@Autowired
	CompetencyService competencyService;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public JobDescription findByJobDescriptionAndCompanyId(String jobDescName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByJobDescNameAndCompanyId(jobDescName, companyId);
	}

	@Override
	public List<JobDescription> findJobDescriptionsForCompany(String companyId) {
		// TODO Auto-generated method stub
		return repo.findJobDescriptionsForCompany(companyId);
	}

	@Override
	public Page<JobDescription> findJobDescriptionsByCompanyId(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findJobDescriptionsByCompanyId(companyId, pageable);
	}

	private Set<Competency> resolve(Set<Competency> competencies){
		Set<Competency> competencies2 = new HashSet<>();
			for(Competency competency : competencies) {
				System.out.println("comp "+competency.getCompetency()+" parent "+competency.getParentCompetency());
				//Competency competency2 = competencyService.findByCompetency(competency.getCompetency(), competency.getCompanyId());
				Competency competency2 = competencyService.findByCompetencyAndParentCompetency(competency.getCompetency(), competency.getParentCompetency(), competency.getCompanyId());
					if(competency2 == null) {
						competency2 = competencyService.saveOrUpdate(competency);
					}
					competencies2.add(competency2);
			}
		
		return competencies2;
	}

	@Override
	public JobDescription saveOrUpdate(JobDescription jobDesc) {
		Objects.requireNonNull(jobDesc);
		Objects.requireNonNull(jobDesc.getJobDescName());
		Objects.requireNonNull(jobDesc.getCompanyId());
		JobDescription jobDesc2 = findByJobDescriptionAndCompanyId(jobDesc.getJobDescName(), jobDesc.getCompanyId());
			if(jobDesc2 == null) {
				jobDesc.setCompetencies(resolve(jobDesc.getCompetencies()));
				jobDesc.setCreateDate(new Date());
				return repo.save(jobDesc);
			}
			else {
				jobDesc.setCreateDate(jobDesc2.getCreateDate());
				jobDesc.setUpdateDate(new Date());
				jobDesc.setId(jobDesc2.getId());
				Set<Competency> competencies = resolve(jobDesc.getCompetencies());
				//role.setCompetencies(resolve(role.getCompetencies()));
				jobDesc2.setCompetencies(null);
				jobDesc.setCompetencies(competencies);
				mapper.map(jobDesc, jobDesc2);
				return repo.save(jobDesc2);
			}
	}
}
