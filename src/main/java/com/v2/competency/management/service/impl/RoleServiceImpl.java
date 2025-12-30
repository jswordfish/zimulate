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
import com.v2.competency.management.entities.Role;
import com.v2.competency.management.repos.RoleRepo;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
	@Autowired
	RoleRepo repo;
	
	@Autowired
	CompetencyService competencyService;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public Role findByRoleNameAndCompanyId(String roleName, String companyId) {
		return repo.findByRoleNameAndCompanyId(roleName, companyId);
	}

	@Override
	public List<Role> findRolesForCompany(String companyId) {
		return repo.findRolesForCompany(companyId);
	}

	@Override
	public Page<Role> findRolesByCompanyId(String companyId, Pageable pageable) {
		return repo.findRolesByCompanyId(companyId, pageable);
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
	public Role saveOrUpdate(Role role) {
		Objects.requireNonNull(role);
		Objects.requireNonNull(role.getRoleName());
		Objects.requireNonNull(role.getCompetencies());
		Role role2 = findByRoleNameAndCompanyId(role.getRoleName(), role.getCompanyId());
			if(role2 == null) {
				role.setCompetencies(resolve(role.getCompetencies()));
				role.setCreateDate(new Date());
				return repo.save(role);
			}
			else {
				role.setCreateDate(role2.getCreateDate());
				role.setUpdateDate(new Date());
				role.setId(role2.getId());
				Set<Competency> competencies = resolve(role.getCompetencies());
				//role.setCompetencies(resolve(role.getCompetencies()));
				role2.setCompetencies(null);
				role.setCompetencies(competencies);
				mapper.map(role, role2);
				return repo.save(role2);
			}
	}

}
