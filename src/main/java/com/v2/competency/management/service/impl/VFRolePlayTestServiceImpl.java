	package com.v2.competency.management.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.RoleplayAnalysisStructure;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.entities.VFRolePlayTestSession;
import com.v2.competency.management.repos.VFRolePlayTestRepo;
import com.v2.competency.management.repos.VFRolePlayTestSessionRepo;
import com.v2.competency.management.service.VFRolePlayTestService;

@Service
@Transactional
public class VFRolePlayTestServiceImpl implements VFRolePlayTestService{
	
	@Autowired
	VFRolePlayTestRepo repo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	


//	@Override
//	public VFRolePlayTest findUniqueRecord(String testName, String competency, String parentCompetency,
//			String questionText, String companyId) {
//		return repo.findUniqueRecord(testName, competency, parentCompetency, questionText, companyId);
//	}

	@Override
	public List<VFRolePlayTest> findRolePlayTestsForCompetency(String competency, String parentCompetency,
			String companyId) {
		return repo.findRolePlayTestsForCompetency(competency, parentCompetency, companyId);
	}
	
	@Override
	public VFRolePlayTest findRolePlayTestsByTestName(String companyId, String testName) {
		return repo.findRolePlayTestsByTestName(companyId, testName);
	}

	@Override
	public Page<VFRolePlayTest> getRolePlayTestsByCompanyId(String companyId, Pageable pageable) {
		return repo.getRolePlayTestsByCompanyId(companyId, pageable);
	}

	@Override
	public VFRolePlayTest saveOrUpdate(VFRolePlayTest test) {
		if(test.getRoleplayAnalysisStructure() == null) {
			throw new RuntimeException("Specifying different types of role play is mandatory");
		}
		//VFRolePlayTest test2 = findUniqueRecord(test.getTestName(), test.getCompetency(), test.getParentCompetency(), test.getQuestionText(), test.getCompanyId());
		VFRolePlayTest test2 = findUniqueRecord(test.getTestName(),test.getCompanyId() );
		if(test2 == null) {
			test.setCreateDate(new Date());
			return repo.save(test);
		}
		else {
			test.setId(test2.getId());
			test.setCreateDate(test2.getCreateDate());
			test.setUpdateDate(new Date());
			RoleplayAnalysisStructure structureExisting = test2.getRoleplayAnalysisStructure();
			RoleplayAnalysisStructure structureNew = test.getRoleplayAnalysisStructure();
			if(structureExisting != null) {
				structureNew.setId(structureExisting.getId());
				structureNew.setCreateDate(structureExisting.getCreateDate());
				structureNew.setUpdateDate(new Date());
				mapper.map(structureNew, structureExisting);
				test.setRoleplayAnalysisStructure(structureExisting);
			}
			else {
				structureNew.setCreateDate(new Date());
				test.setRoleplayAnalysisStructure(structureNew);
			}
			
			mapper.map(test, test2);
			return repo.save(test2);
		}
		
	}

	@Override
	public VFRolePlayTest findUniqueRecord(String testName, String companyId) {
		// TODO Auto-generated method stub
		return repo.findUniqueRecord(testName, companyId);
	}

	@Override
	public List<VFRolePlayTest> findPublishedTests(String companyId) {
		// TODO Auto-generated method stub
		return repo.findPublishedTests(companyId);
	}
	
	private static final int PAGE_SIZE = 10;
	
	@Override
    public PaginatedResponseDto getAllRolePlayTests(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("testName").ascending());
        Page<VFRolePlayTest> tests = repo.findAll(pageable);

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom(page * PAGE_SIZE + 1);
        dto.setRecordsTo(page * PAGE_SIZE + tests.getNumberOfElements());
        dto.setTotalNumberOfRecords((int) tests.getTotalElements());
        dto.setTotalNumberOfPages(tests.getTotalPages());
        dto.setSelectedPage(page + 1);
        dto.setList(tests.getContent());

        return dto;
    }

	@Override
	public PaginatedResponseDto getRolePlayTestsByCompanyAndIndustry(String companyId, String industries, int page) {
	    Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("testName").ascending());

	    List<String> industryList = Arrays.stream(industries.split(","))
	            .map(String::trim)
	            .map(String::toLowerCase)
	            .collect(Collectors.toList());

	    Page<VFRolePlayTest> tests = repo.findByCompanyIdAndIndustriesIgnoreCase(companyId, industryList, pageable);

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setRecordsFrom(page * PAGE_SIZE + 1);
	    dto.setRecordsTo(page * PAGE_SIZE + tests.getNumberOfElements());
	    dto.setTotalNumberOfRecords((int) tests.getTotalElements());
	    dto.setTotalNumberOfPages(tests.getTotalPages());
	    dto.setSelectedPage(page + 1);
	    dto.setList(tests.getContent());

	    return dto;
	}
	
	private static final int DEFAULT_PAGE_SIZE = 10;

	@Override
	public PaginatedResponseDto getRolePlayTestsFiltered(String companyId, String industries, String search,
			String sort, int page, int size) {
		// TODO Auto-generated method stub
		// Handle industries (optional)
		// Handle industries (optional)
	    List<String> industryList = null;
	    boolean isIndustriesEmpty = true;
	    if (industries != null && !industries.trim().isEmpty()) {
	        industryList = Arrays.stream(industries.split(","))
	                             .map(String::trim)
	                             .map(String::toLowerCase)
	                             .collect(Collectors.toList());
	        isIndustriesEmpty = false;
	    }

	    // Handle sorting
	    Sort sortObj = Sort.by("testName").ascending(); // default
	    if (sort != null && !sort.trim().isEmpty()) {
	        List<Sort.Order> orders = Arrays.stream(sort.split(","))
	                .map(String::trim)
	                .map(s -> {
	                    String[] parts = s.split("=");
	                    if (parts.length == 2) {
	                        String field = parts[0].trim();
	                        String direction = parts[1].trim().toLowerCase();
	                        return direction.equals("desc")
	                                ? Sort.Order.desc(field)
	                                : Sort.Order.asc(field);
	                    } else {
	                        return Sort.Order.asc(s); // fallback
	                    }
	                })
	                .collect(Collectors.toList());
	        sortObj = Sort.by(orders);
	    }

	    Pageable pageable = PageRequest.of(page, size > 0 ? size : 10, sortObj);

	    // Run query
	    Page<VFRolePlayTest> tests = repo.findByCompanyIdAndFilters(
	            companyId, industryList, isIndustriesEmpty, search, pageable);

	    // Build response
	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setRecordsFrom(page * size + 1);
	    dto.setRecordsTo(page * size + tests.getNumberOfElements());
	    dto.setTotalNumberOfRecords((int) tests.getTotalElements());
	    dto.setTotalNumberOfPages(tests.getTotalPages());
	    dto.setSelectedPage(page + 1);
	    dto.setList(tests.getContent());

	    return dto;
	}
	
	@Override
    public PaginatedResponseDto getAllIndustriesPaginated(String companyId, int page) {
        List<String> allIndustries = repo.findDistinctIndustriesByCompanyId(companyId);

        int totalRecords = allIndustries.size();
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        int fromIndex = Math.min(page * PAGE_SIZE, totalRecords);
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalRecords);

        List<String> paginatedList = allIndustries.subList(fromIndex, toIndex);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(fromIndex + 1);
        response.setRecordsTo(toIndex);
        response.setTotalNumberOfRecords(totalRecords);
        response.setTotalNumberOfPages(totalPages);
        response.setSelectedPage(page + 1);
        response.setList(paginatedList);

        return response;
    }

	@Override
	public Page<VFRolePlayTest> searchTrainingRolePlays(String companyId,  Pageable pageable) {
		return repo.searchTrainingRolePlays(companyId, pageable);
	}

	@Override
	public Page<VFRolePlayTest> searchAssessmentRolePlays(String companyId,  Pageable pageable) {
		return repo.searchAssessmentRolePlays(companyId, pageable);
	}

	@Override
	public PaginatedResponseDto searchTrainingRolePlays(
	        String companyId,
	        String search,
	        int page,
	        int size) {

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("id").descending()
	    );

	    Page<VFRolePlayTest> tests =
	            repo.searchTrainingRolePlays(
	                    companyId,
	                    search,
	                    pageable
	            );

	    PaginatedResponseDto dto = new PaginatedResponseDto();

	    int recordsFrom = tests.getTotalElements() == 0
	            ? 0
	            : (page * size) + 1;

	    int recordsTo = Math.min(
	            (page + 1) * size,
	            (int) tests.getTotalElements()
	    );

	    dto.setRecordsFrom(recordsFrom);
	    dto.setRecordsTo(recordsTo);
	    dto.setTotalNumberOfRecords((int) tests.getTotalElements());
	    dto.setTotalNumberOfPages(tests.getTotalPages());
	    dto.setSelectedPage(page);
	    dto.setList(tests.getContent());

	    return dto;
	}
	
	@Override
	public PaginatedResponseDto searchAssessmentRolePlays(
	        String companyId,
	        String search,
	        int page,
	        int size) {

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("id").descending()
	    );

	    Page<VFRolePlayTest> tests =
	            repo.searchAssessmentRolePlays(
	                    companyId,
	                    search,
	                    pageable
	            );

	    PaginatedResponseDto dto = new PaginatedResponseDto();

	    int recordsFrom = tests.getTotalElements() == 0
	            ? 0
	            : (page * size) + 1;

	    int recordsTo = Math.min(
	            (page + 1) * size,
	            (int) tests.getTotalElements()
	    );

	    dto.setRecordsFrom(recordsFrom);
	    dto.setRecordsTo(recordsTo);
	    dto.setTotalNumberOfRecords((int) tests.getTotalElements());
	    dto.setTotalNumberOfPages(tests.getTotalPages());
	    dto.setSelectedPage(page);
	    dto.setList(tests.getContent());

	    return dto;
	}
}
