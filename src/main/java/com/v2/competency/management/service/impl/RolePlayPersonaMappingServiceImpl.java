package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.RolePlayPersonaMapping;
import com.v2.competency.management.repos.RolePlayPersonaMappingRepo;
import com.v2.competency.management.service.RolePlayPersonaMappingService;
@Service
@Transactional
public class RolePlayPersonaMappingServiceImpl implements RolePlayPersonaMappingService{
	
	@Autowired
	RolePlayPersonaMappingRepo mappingRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	
	@Override
	public List<RolePlayPersonaMapping> findPersonasForTypeAndCompanyId(String testIdentifier,
			String companyId) {
		return mappingRepo.findPersonasForTypeAndCompanyId(testIdentifier, companyId);
	}

	private void validate(RolePlayPersonaMapping mapping) {
		Objects.requireNonNull(mapping.getCompanyId());
		Objects.requireNonNull(mapping.getPersona());
		Objects.requireNonNull(mapping.getPersonaDescription());
		Objects.requireNonNull(mapping.getVoiceId());
		Objects.requireNonNull(mapping.getType());
	}

	@Override
	public RolePlayPersonaMapping addRolePlayPersonaMapping(RolePlayPersonaMapping mapping) {
		validate(mapping);
		Date date = new Date();
		mapping.setCreateDate(date);
		return mappingRepo.save(mapping);
	}

	@Override
	public RolePlayPersonaMapping updateRolePlayPersonaMapping(RolePlayPersonaMapping mapping) {
		Objects.requireNonNull(mapping.getId());
		validate(mapping);
		RolePlayPersonaMapping mapping2 =  mappingRepo.findById(mapping.getId()).get();
		if(mapping2 == null) {
			throw new RuntimeException("ID "+mapping.getId()+" does not exist");
		}
		mapper.map(mapping, mapping2);
		return mappingRepo.save(mapping2);
	}

}
