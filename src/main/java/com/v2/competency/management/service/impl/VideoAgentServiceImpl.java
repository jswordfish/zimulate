package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.VideoAgent;
import com.v2.competency.management.repos.VideoAgentRepo;
import com.v2.competency.management.service.VideoAgentService;
@Service
@Transactional
public class VideoAgentServiceImpl implements VideoAgentService{
	
	@Autowired
	VideoAgentRepo agentRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

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

}
