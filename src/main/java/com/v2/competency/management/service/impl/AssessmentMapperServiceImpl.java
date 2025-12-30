package com.v2.competency.management.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.dtos.AssessmentTraversalPath;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.Path;
import com.v2.competency.management.dtos.UserWithTestDto;
import com.v2.competency.management.entities.AssessmentMapper;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.repos.AssessmentMapperRepo;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.UserService;
@Service
@Transactional
public class AssessmentMapperServiceImpl implements AssessmentMapperService{

	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	@Autowired
	AssessmentMapperRepo repo;
	
	@Autowired
	UserService userService;
	 
	
	@Override
	public AssessmentMapper findByEmailAndTestNameAndPaths(String email, String testName, String path1, String path2,
			String path3, String path4, String path5, String companyId) {
		return repo.findByEmailAndTestNameAndPaths(email, testName, path1, path2, path3, path4, path5, companyId);
	}

	@Override
	public List<AssessmentMapper> findAssessmentsForUserByPath1(String email,  String path1,
			String companyId) {
		return repo.findAssessmentsForUserByPath1(email, path1, companyId);
	}

	@Override
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2(String email, String path1,
			String path2, String companyId) {
		return repo.findAssessmentsForUserByPath1AndPath2(email, path1, path2, companyId);
	}

	@Override
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3(String email,
			String path1, String path2, String path3, String companyId) {
		return repo.findAssessmentsForUserByPath1AndPath2AndPath3(email, path1, path2, path3, companyId);
	}

	@Override
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(String email,
			String path1, String path2, String path3, String path4, String companyId) {
		return repo.findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(email, path1, path2, path3, path4, companyId);
	}

	@Override
	public List<AssessmentMapper> findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(String email,
			String path1, String path2, String path3, String path4, String path5, String companyId) {
		return repo.findAssessmentsForUserByPath1AndPath2AndPath3AndPath4(email, path1, path2, path3, path4, path5, companyId);
	}

	@Override
	public AssessmentMapper saveOrUpdate(AssessmentMapper assessmentMapper) {
		Objects.requireNonNull(assessmentMapper.getCompanyId());
		Objects.requireNonNull(assessmentMapper.getEmail());
		Objects.requireNonNull(assessmentMapper.getTestName());
		//Objects.requireNonNull(assessmentMapper.getTestName());
		Objects.requireNonNull(assessmentMapper.getPath1());
		
		
		AssessmentMapper assessmentMapper2 = findByEmailAndTestNameAndPaths(assessmentMapper.getEmail(), assessmentMapper.getTestName(), assessmentMapper.getPath1(), 
				assessmentMapper.getPath2(), assessmentMapper.getPath3(),
				assessmentMapper.getPath4(), assessmentMapper.getPath5(), assessmentMapper.getCompanyId());
		System.out.println(assessmentMapper.getEmail() +" "+assessmentMapper.getTestName()+" "+assessmentMapper.getPath1()+" "
				+assessmentMapper.getPath2()+" "+assessmentMapper.getPath3()+" "+assessmentMapper.getPath4()+" "+assessmentMapper.getPath5()
				+" "+assessmentMapper.getCompanyId());
		System.out.println("assessmentMapper2 "+assessmentMapper2);
		
			if(assessmentMapper2 == null) {
				assessmentMapper.setCreateDate(new Date());
				//call assessment api to fetch assessment link
				//set the link on assessmentMapper
				return repo.save(assessmentMapper);
			}
			else {
				assessmentMapper.setCreateDate(assessmentMapper2.getCreateDate());
				assessmentMapper.setUpdateDate(new Date());
				assessmentMapper.setId(assessmentMapper2.getId());
				mapper.map(assessmentMapper, assessmentMapper2);
				//call assessment api to fetch assessment link
				//set the link on assessmentMapper
				return repo.save(assessmentMapper2);
			}
	}

	@Override
	public AssessmentTraversalPath computeForUser(String email, String companyId) {
		List<String> levels1 =  repo.fetchDistinctPath1sForUser(email, companyId);
		if(levels1.size() == 0) {
			return AssessmentTraversalPath.builder().email(email)
					.companyId(companyId)
					.build();
		}
		else {
			List<Path> paths1 = new ArrayList<>();
			for(String p : levels1) {
				Path path1 = Path.builder().pathName(p).build();
				path1.setChilds(new ArrayList<>());
				paths1.add(path1);
				List<String> levels2 = repo.fetchDistinctPath2sForUser(email, companyId, p);
				if(levels2.size() != 0) {
					//List<Path> paths2 = new ArrayList<>();
					for(String p2 : levels2) {
						if(!p2.equalsIgnoreCase("NA")) {
							Path path2 = Path.builder().pathName(p2).build();
							path2.setChilds(new ArrayList<>());
							path1.getChilds().add(path2);
							List<String> levels3 = repo.fetchDistinctPath3sForUser(email, companyId, p, p2);
								if(levels3.size() != 0) {
									
									//start
									//List<Path> paths3 = new ArrayList<>();
									for(String p3 : levels3) {
										if(!p3.equalsIgnoreCase("NA")) {
											Path path3 = Path.builder().pathName(p3).build();
											path3.setChilds(new ArrayList<>());
											path2.getChilds().add(path3);
											List<String> levels4 = repo.fetchDistinctPath4sForUser(email, companyId, p, p2, p3);
												if(levels4.size() != 0) {
													
													//start
													//List<Path> paths4 = new ArrayList<>();
													for(String p4 : levels4) {
														if(!p4.equalsIgnoreCase("NA")) {
															Path path4 = Path.builder().pathName(p4).build();
															path4.setChilds(new ArrayList<>());
															path3.getChilds().add(path4);
															List<String> levels5 = repo.fetchDistinctPath5sForUser(email, companyId, p, p2, p3, p4);
																if(levels4.size() != 0) {
																	
																	//start
																	//List<Path> paths5 = new ArrayList<>();
																	for(String p5 : levels5) {
																		if(!p5.equalsIgnoreCase("NA")) {
																			Path path5 = Path.builder().pathName(p5).build();
																			path5.setChilds(new ArrayList<>());
																			path4.getChilds().add(path5);
																		}
																		
																		
																	}
																	//end
																}
														}
														
													}
													//end
												}
										}
										
									}
									
									//end
									
									
								}
						}
						
					}
				}
			}
			return AssessmentTraversalPath.builder().email(email)
					.companyId(companyId)
					.level1Paths(paths1)
					.build();
		}
	}

	@Override
	public Page<AssessmentMapper> findAssessmentsForUser(String companyId, String email, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findAssessmentsForUser(companyId, email, pageable);
	}

	@Override
	public Page<String> getListOfUsersAssignedTests(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		Page<String> users =  repo.getListOfUsersAssignedTests(companyId, pageable);
		
		return users;
	}

	@Override
	public List<AssessmentMapper> findRolesForUserAssignedAssessments(String email, String companyId) {
		// TODO Auto-generated method stub
		return repo.findRolesForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> findCompetenciesForUserAssignedAssessments(String email, String companyId) {
		// TODO Auto-generated method stub
		return repo.findCompetenciesForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> findJobDescriptionsForUserAssignedAssessments(String email, String companyId) {
		// TODO Auto-generated method stub
		return repo.findJobDescriptionsForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> find360DegreeSurveysForUserAssignedAssessments(String email, String companyId) {
		// TODO Auto-generated method stub
		return repo.find360DegreeSurveysForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> findConsolidatedAssessmentsForUserAssignedAssessments(String email,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findConsolidatedAssessmentsForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> findConsolidated360DegreeSurveyForUserAssignedAssessments(String email,
			String companyId) {
		// TODO Auto-generated method stub
		return repo.findConsolidated360DegreeSurveyForUserAssignedAssessments(email, companyId);
	}

	@Override
	public List<AssessmentMapper> findConsolidatedReviewers(String testName, String reviewed_user_email,
			String type_path2, String companyId) {
		// TODO Auto-generated method stub
		return repo.findConsolidatedReviewers(testName, reviewed_user_email, type_path2, companyId);
	}

	@Override
	public Page<AssessmentMapper> findExternalAssessmentTakers(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findExternalAssessmentTakers(companyId, pageable);
	}

	@Override
	public Page<AssessmentMapper> findInternalAssessmentTakers(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findInternalAssessmentTakers(companyId, pageable);
	}

	@Override
	public Page<AssessmentMapper> findExternalAndInternalAssessmentTakers(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.findExternalAndInternalAssessmentTakers(companyId, pageable);
	}

	@Override
	public Page<UserWithTestDto> getUsersByTestName(String testName, int page) {
        int pageSize = 10; 
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<User> userPage = repo.findUsersByTestName(testName, pageable);

        
        return userPage.map(user -> new UserWithTestDto(testName, user));
    }
	
	@Override
	public PaginatedResponseDto findAssessmentsForUserWithFilters(
	        String companyId,
	        String email,
	        String search,
	        String sort,
	        int page,
	        int size) {

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
	                        return Sort.Order.asc(s);
	                    }
	                })
	                .collect(Collectors.toList());
	        sortObj = Sort.by(orders);
	    }

	    Pageable pageable = PageRequest.of(page, size > 0 ? size : 10, sortObj);

	    // Call repo query with filters
	    Page<AssessmentMapper> pageResult =
	            repo.findAssessmentsForUserWithFilters(companyId, email, search, pageable);

	    // Build response
	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setRecordsFrom(page * size + 1);
	    dto.setRecordsTo(page * size + pageResult.getNumberOfElements());
	    dto.setTotalNumberOfRecords((int) pageResult.getTotalElements());
	    dto.setTotalNumberOfPages(pageResult.getTotalPages());
	    dto.setSelectedPage(page + 1);
	    dto.setList(pageResult.getContent());

	    return dto;
	}
	
	@Override
    public PaginatedResponseDto getTestsAssignedByManager(
            String assignedBy, String companyId, int page, int size, String sort) {

        // Parse sorting
        Sort sortObj = Sort.by("createDate").descending(); // default
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
                            return Sort.Order.asc(s);
                        }
                    })
                    .collect(Collectors.toList());
            sortObj = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<AssessmentMapper> pageResult = repo.findByAssignedBy(assignedBy, companyId, pageable);

        // Build response dto
        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(page * size + pageResult.getNumberOfElements());
        dto.setTotalNumberOfRecords((int) pageResult.getTotalElements());
        dto.setTotalNumberOfPages(pageResult.getTotalPages());
        dto.setSelectedPage(page + 1);
        dto.setList(pageResult.getContent());

        return dto;
    }

}
