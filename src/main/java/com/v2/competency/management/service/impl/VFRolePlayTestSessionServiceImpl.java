package com.v2.competency.management.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.service.VFRolePlayTestSessionService;

@Service
public class VFRolePlayTestSessionServiceImpl implements VFRolePlayTestSessionService {
	
	@Autowired
	VFRolePlayTestSessionRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public VFRolePlayTestSession findVFRolePlayTestSessionByEmail(String email, String companyId, String testIdentifier,
			Integer attempt) {
		// TODO Auto-generated method stub
		return repo.findVFRolePlayTestSessionByEmail(email, companyId, testIdentifier, attempt);
	}

	@Override
	public VFRolePlayTestSession saveOrUpdate(VFRolePlayTestSession session) {
		Objects.requireNonNull(session);
		Objects.requireNonNull(session.getCompanyId());
		Objects.requireNonNull(session.getEmail());
		Objects.requireNonNull(session.getAttempt());
		System.out.println("111, method is being saved or updated");
		VFRolePlayTestSession session2 = findVFRolePlayTestSessionByEmail(session.getEmail(), session.getCompanyId(), session.getTestIdentifier(), session.getAttempt());
		System.out.println("session :- "+session2);
		if(session2 == null) {
			session.setCreateDate(new Date());
			System.out.println("Creating session");
			session = repo.save(session);
			System.out.println("Created session with id :- "+session.getId());
//			return repo.save(session);
			return session;
		}
		else {
			session.setId(session2.getId());
			session.setCreateDate(session2.getCreateDate());
			session.setUpdateDate(new Date());
			System.out.println("mapping session");
			mapper.map(session, session2);
			session2 = repo.save(session2);
			System.out.println("Updated session2 with id :- "+session2.getId());
			return session2;
		}
	}
	
	
	@Override
	public PaginatedResponseDto getLatestRolePlayAttempts(

	        String companyId,
	        String email,
	        String testName,
	        String search,
	        String sort,
	        int page,
	        int size) {

	    // Default sort: id desc
	    Sort sortSpec = Sort.by("id").descending();

	    // Parse sort input: name=asc
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

	    if (search != null && search.trim().isEmpty()) {
	        search = null;
	    }

	    Page<VFRolePlayTestSession> pageResult =
	            repo.findRolePlayAttempts(
	                    companyId,
	                    testName,
	                    email,
	                    search,
	                    pageable
	            );

	    PaginatedResponseDto dto = new PaginatedResponseDto();

	    dto.setSelectedPage(page);

	    dto.setTotalNumberOfRecords(
	            (int) pageResult.getTotalElements()
	    );

	    dto.setTotalNumberOfPages(
	            pageResult.getTotalPages()
	    );

	    if (pageResult.getTotalElements() == 0) {

	        dto.setRecordsFrom(0);
	        dto.setRecordsTo(0);

	    } else {

	        dto.setRecordsFrom(page * size + 1);

	        dto.setRecordsTo(
	                Math.min(
	                        (page + 1) * size,
	                        (int) pageResult.getTotalElements()
	                )
	        );
	    }

	    dto.setList(pageResult.getContent());

	    return dto;
	}
	


	@Override
	public Integer findCountOfSessionsForUserForTest(String email, String companyId, String testName) {
		// TODO Auto-generated method stub
		return repo.findCountOfSessionsForUserFotTestByName(email, companyId, testName);
	}

	@Override
	public Page<VFRolePlayTestSession> findUserSessionsForTest(String testIdentifier, String companyId,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findUserSessionsForTest(testIdentifier, companyId, pageable);
	}

	@Override
	public Page<VFRolePlayTestSession> findUserSessionsByEmailAndTestName(
	        String email, String companyId, String testName, Pageable pageable) {
	    return repo.findUserSessionsByEmailAndTestName(email, companyId, testName, pageable);
	}

	@Override
	public VFRolePlayTestSession findVFRolePlayTestSessionById(Long id) {
		// TODO Auto-generated method stub
		return repo.findById(id).get();
	}
	
	@Override
    public Page<VFRolePlayTestSession> getAllTestSessions(Pageable pageable) {
        return repo.findAll(pageable);
    }
	
	@Override
	public Page<VFRolePlayTestSession> findUserSessions(
	        String email,
	        String companyId,
	        String testName,
	        List<String> personaList,
	        boolean isPersonasEmpty,
	        String difficultyLevel,  
	        Pageable pageable) {
	    return repo.findUserSessions(email, companyId, testName, personaList, isPersonasEmpty, difficultyLevel, pageable);
	}

	@Override
	public List<VFRolePlayTestSession> seatchEmailUserSessionsForTest(String testIdentifier, String companyId,
			String containsText) {
		return repo.seatchEmailUserSessionsForTest(testIdentifier, companyId, containsText);
	}

	@Override
	public List<VFRolePlayTestSession> findRoleplaySessionsByWorkflowId(Long workflowSessionId) {
		return repo.findRoleplaySessionsByWorkflowSessionId(workflowSessionId);
	}

	@Override
	public Page<VFRolePlayTestSession> searchAssessmentsForRoleplay(String companyId, String testIdentifier,
			String search, Pageable pageable) {
		return repo.searchAssessmentsForRoleplay(companyId, testIdentifier, search, pageable);
	}
}
