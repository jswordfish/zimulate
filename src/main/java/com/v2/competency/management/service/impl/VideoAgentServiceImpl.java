package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.service.VideoAgentService;
@Service
@Transactional
public class VideoAgentServiceImpl implements VideoAgentService{
	
	@Autowired
	VideoAgentRepo agentRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	Logger logger = LoggerFactory.getLogger(VideoAgentServiceImpl.class);

	@Override
	public VideoAgent findByVideoAgentIdentifier(String name, String industry, String agentType, String companyId) {
		return agentRepo.findByVideoAgentIdentifier(name, industry, agentType, companyId);
	}

	@Override
	public VideoAgent saveOrUpdate(VideoAgent agent) {
		VideoAgent agent2 = findByVideoAgentIdentifier(agent.getName(), agent.getIndustry(), agent.getAgentType(), agent.getCompanyId());
			if(agent2 == null) {
				agent.setCreateDate(new Date());
				return agentRepo.save(agent);
			}
			else {
				agent.setUpdateDate(new Date());
				agent.setId(agent2.getId());
				mapper.map(agent, agent2);
				return agentRepo.save(agent2);
			}
	}

	@Override
	public Page<VideoAgent> findVideoAgentsByCompanyId(String companyId, Pageable pageable) {
		return agentRepo.findVideoAgentsByCompanyId(companyId, pageable);
	}

	@Override
	public Page<VideoAgent> searchVideoAgents(String search, String companyId, Pageable pageable) {
		return agentRepo.searchVideoAgents(search, companyId, pageable);
	}

	@Override
	public List<String> findIndustries(String companyId) {
		return agentRepo.findIndustries(companyId);
	}

	
	@Override
	public PaginatedResponseDto searchVideoAgents(
	        String search,
	        String companyId,
	        int page,
	        int size) {

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("id").descending()
	    );

	    Page<VideoAgent> agents =
	            agentRepo.searchVideoAgents(
	                    search,
	                    companyId,
	                    pageable
	            );

	    PaginatedResponseDto dto = new PaginatedResponseDto();

	    int recordsFrom = agents.getTotalElements() == 0
	            ? 0
	            : (page * size) + 1;

	    int recordsTo = Math.min(
	            (page + 1) * size,
	            (int) agents.getTotalElements()
	    );

	    dto.setRecordsFrom(recordsFrom);
	    dto.setRecordsTo(recordsTo);
	    dto.setTotalNumberOfRecords((int) agents.getTotalElements());
	    dto.setTotalNumberOfPages(agents.getTotalPages());
	    dto.setSelectedPage(page);
	    dto.setList(agents.getContent());

	    return dto;
	}
}
