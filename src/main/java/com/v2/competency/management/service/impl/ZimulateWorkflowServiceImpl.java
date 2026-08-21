package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.ZimulateWorkflow;
import com.v2.competency.management.repos.ZimulateWorkflowRepo;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.ZimulateWorkflowService;
@Service
@Transactional
public class ZimulateWorkflowServiceImpl implements ZimulateWorkflowService{
	
	@Autowired
	ZimulateWorkflowRepo repo;
	
	@Autowired
	TenantService tenantService;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public ZimulateWorkflow findUniqueZimulateWorkflow(String name, String industry, String companyId) {
		return repo.findUniqueZimulateWorkflow(name, industry, companyId);
	}

	@Override
	public ZimulateWorkflow saveOrUpdate(ZimulateWorkflow workflow) {
		ZimulateWorkflow  workflow2 = findUniqueZimulateWorkflow(workflow.getName(), workflow.getIndustry(), workflow.getCompanyId());
			if(workflow2 == null) {
				workflow.setCreateDate(new Date());
				return repo.save(workflow);
			}
			
			workflow.setId(workflow2.getId());
			workflow.setUpdateDate(new Date());
			workflow.setCreateDate(workflow2.getCreateDate());
			mapper.map(workflow, workflow2);
		return repo.save(workflow2);
	}
	
	@Override
    public boolean canUpdateOrDeleteWorkflow(
            Long workflowId,
            String companyId) {
		
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
		
		if(tenant==null) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - "+companyId);
			
		}

        Optional<ZimulateWorkflow> workflow =
                repo.findByIdAndCompanyId(
                        workflowId,
                        companyId
                );

        if (workflow.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Workflow not found"
            );
        }

        
        if (Boolean.TRUE.equals(workflow.get().getComplete())) {
            return false;
        }

        return true;
    }


    @Override
    public void deleteWorkflow(
            Long workflowId,
            String companyId) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(companyId);
		
		if(tenant==null) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - "+companyId);
			
		}

        Optional<ZimulateWorkflow> workflow =
                repo.findByIdAndCompanyId(
                        workflowId,
                        companyId
                );

        if (workflow.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Workflow not found"
            );
        }

        
        if (Boolean.TRUE.equals(workflow.get().getComplete())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed workflow cannot be deleted"
            );
        }

        repo.delete(workflow.get());
    }

	@Override
	public Page<ZimulateWorkflow> findAllWorkflows(String companyId, Pageable pageable) {
		return repo.findAllWorkflows(companyId, pageable);
	}
	
	@Override
	public PaginatedResponseDto findAllWorkflows(
	        String companyId,
	        String search,
	        int page,
	        int size) {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<ZimulateWorkflow> workflowPage = repo.findAllWorkflows(companyId, search, pageable);

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    
	    int recordsFrom = workflowPage.getTotalElements() == 0 ? 0 : (page * size) + 1;
	    int recordsTo = Math.min((page + 1) * size, (int) workflowPage.getTotalElements());

	    dto.setRecordsFrom(recordsFrom);
	    dto.setRecordsTo(recordsTo);
	    dto.setTotalNumberOfRecords((int) workflowPage.getTotalElements());
	    dto.setTotalNumberOfPages(workflowPage.getTotalPages());
	    dto.setSelectedPage(page);
	    dto.setList(workflowPage.getContent());

	    return dto;
	}

}
