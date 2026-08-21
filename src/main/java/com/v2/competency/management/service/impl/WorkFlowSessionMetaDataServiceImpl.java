package com.v2.competency.management.service.impl;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.WorkFlowSessionMetaData;
import com.v2.competency.management.repos.WorkFlowSessionMetaDataRepository;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.WorkFlowSessionMetaDataService;

@Service
@Transactional
public class WorkFlowSessionMetaDataServiceImpl
        implements WorkFlowSessionMetaDataService {

    @Autowired
    private WorkFlowSessionMetaDataRepository repo;
    
    @Autowired
	TenantService tenantService;


    @Override
    public WorkFlowSessionMetaData saveWorkFlowSessionMetaData(
            WorkFlowSessionMetaData metaData) {
    	
		Tenant tenant = tenantService.findTenantByCompanyId(metaData.getCompanyId());
		
		if(tenant==null) {
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - "+metaData.getCompanyId());
			
		}

        return repo.save(metaData);
    }


    @Override
    public PaginatedResponseDto listWorkFlowSessionMetaData(

            String companyId,

            Long rolePlaySessionId,

            Long elevenLabsSessionId,

            String rolePlayTestName,

            Integer attempt,

            String email,

            LocalDateTime startTime,

            LocalDateTime endTime,

            Integer durationInMinutes,

            Long tokenUsed,

            String search,

            String sort,

            int page,

            int size
    ) {

        
        Sort sortSpec = Sort.by("id").descending();


        
        if (sort != null && !sort.isEmpty()) {

            String[] parts = sort.split("=");

            if (parts.length == 2) {

                String field = parts[0];

                String direction = parts[1];

                sortSpec = direction.equalsIgnoreCase("asc")
                        ? Sort.by(field).ascending()
                        : Sort.by(field).descending();
            }
        }


        Pageable pageable = PageRequest.of(
                page,
                size,
                sortSpec
        );


        
        if (rolePlayTestName != null &&
                rolePlayTestName.trim().isEmpty()) {

            rolePlayTestName = null;
        }

        if (email != null &&
                email.trim().isEmpty()) {

            email = null;
        }

        if (search != null &&
                search.trim().isEmpty()) {

            search = null;
        }


        
        Page<WorkFlowSessionMetaData> pageData =
                repo.searchWorkFlowSessionMetaData(

                        companyId,

                        rolePlaySessionId,

                        elevenLabsSessionId,

                        rolePlayTestName,

                        attempt,

                        email,

                        startTime,

                        endTime,

                        durationInMinutes,

                        tokenUsed,

                        search,

                        pageable
                );


        
        PaginatedResponseDto response =
                new PaginatedResponseDto();


        response.setRecordsFrom(
                page * size + 1
        );


        response.setRecordsTo(
                (int) Math.min(
                        (page + 1) * size,
                        pageData.getTotalElements()
                )
        );


        response.setTotalNumberOfRecords(
                (int) pageData.getTotalElements()
        );


        response.setTotalNumberOfPages(
                pageData.getTotalPages()
        );


        response.setSelectedPage(
                page
        );


        response.setList(
                pageData.getContent()
        );


        return response;
    }
}
