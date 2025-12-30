package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.dtos.ConsolidatedAssessmentDto;
import com.v2.competency.management.entities.CompetencyAssessmentMapping;
import com.v2.competency.management.service.CompetencyAssessmentMappingService;
import com.v2.competency.management.service.TenantService;
@RestController
@CrossOrigin
public class CompetencyAssessmentMappingController {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	CompetencyAssessmentMappingService competencyAssessmentMappingService;
	
	@RequestMapping(value="uploadAssessmentMappingForCompetency",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadAssessmentMappingForCompetency( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<CompetencyAssessmentMapping> mappings = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, CompetencyAssessmentMapping.class);
			//System.out.println("Printing List Data: " +mappings);
			for(CompetencyAssessmentMapping map : mappings) {
				map.setCompetency(map.getCompetency().trim());
				if(map.getParentCompetency() != null) {
					map.setParentCompetency(map.getParentCompetency().trim());
						if(map.getParentCompetency().length() == 0) {
							map.setParentCompetency("NA");
						}
				}
				else {
					map.setParentCompetency("NA");
				}
				map.setTestName(map.getTestName().trim());
				if(!companyId.equals(map.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+map.getCompanyId());
				}
				
				map.setCompanyId(companyId);
				competencyAssessmentMappingService.saveOrUpdate(map);
				
			}
			
			 return ResponseEntity.ok("ok");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	@RequestMapping(value="uploadConsolidatedAssessmentMappingForMultipleCompetencies",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadConsolidatedAssessmentMappingForMultipleCompetencies( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<CompetencyAssessmentMapping> mappings = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, CompetencyAssessmentMapping.class);
		Map<String, List<CompetencyAssessmentMapping>> mapping = new HashMap<>();//for validating same test name is there for all competencies in group
		for(CompetencyAssessmentMapping map : mappings) {
			if(map.getConsolidatedAssessmentGroupName() == null || map.getConsolidatedAssessmentGroupName().trim().length() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("assessment for competency  "+map.getCompetency()+" & assessment "+map.getTestName()+" do not have consolidated group name");
			}
			
			if(map.getCompetency() == null || map.getCompetency().trim().length() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Competncy absent for"+map.getConsolidatedAssessmentGroupName());
			}
			
			if(map.getTestName() == null || map.getTestName().trim().length() == 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test name absent for"+map.getConsolidatedAssessmentGroupName());
			}
			
			if(mapping.get(map.getConsolidatedAssessmentGroupName()) == null) {
				List<CompetencyAssessmentMapping> list = new ArrayList<>();
				list.add(map);
				mapping.put(map.getConsolidatedAssessmentGroupName(), list);
			}
			else {
				mapping.get(map.getConsolidatedAssessmentGroupName()).add(map);
			}
		}
		
		validateUniqueTestInGroup(mapping);
		
			//System.out.println("Printing List Data: " +mappings);
			for(CompetencyAssessmentMapping map : mappings) {
				map.setCompetency(map.getCompetency().trim());
				if(map.getParentCompetency() != null) {
					map.setParentCompetency(map.getParentCompetency().trim());
						if(map.getParentCompetency().length() == 0) {
							map.setParentCompetency("NA");
						}
				}
				else {
					map.setParentCompetency("NA");
				}
				
				map.setCompetency(map.getConsolidatedAssessmentGroupName()+"###"+map.getCompetency());
				map.setParentCompetency(map.getConsolidatedAssessmentGroupName()+"###"+map.getParentCompetency());
				
				map.setTestName(map.getTestName().trim());
				if(!companyId.equals(map.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+map.getCompanyId());
				}
				
				map.setCompanyId(companyId);
				map.setConsolidatedAssessments(true);
				competencyAssessmentMappingService.saveOrUpdate(map);
				
			}
			
			 return ResponseEntity.ok("ok");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	private void validateUniqueTestInGroup(Map<String, List<CompetencyAssessmentMapping>> mapping) {
		for(String groupName : mapping.keySet()) {
			List<CompetencyAssessmentMapping> list = mapping.get(groupName);
			String testName = list.get(0).getTestName();
				for(int i=0;i<list.size();i++) {
					if(!list.get(i).getTestName().equals(testName)) {
						throw new RuntimeException("Test Name not unique in the group "+list.get(i).getConsolidatedAssessmentGroupName());
					}
				}
		}
	}
	
	
	@RequestMapping(value="testsForCompetency",method=RequestMethod.GET)  
    public ResponseEntity<?> testsForCompetency(  @RequestParam String companyId, @RequestParam String competency, @RequestParam(required = false) String parentCompetency,
           HttpSession session, @RequestParam String token) throws Exception{  
		competency = competency.trim();
		if(parentCompetency != null && parentCompetency.trim().length() == 0) {
			parentCompetency = "NA";
		}
		
		if(parentCompetency == null) {
			parentCompetency = "NA";
		}
	//	System.out.println("competency "+competency+" par "+parentCompetency+" cid "+companyId);
	 List<CompetencyAssessmentMapping> tests = competencyAssessmentMappingService.findAssessmentsForCompetency(competency, parentCompetency, companyId);
	 return ResponseEntity.ok(tests);
	}
	
	@RequestMapping(value="consolidatedCompetencies",method=RequestMethod.GET)  
    public ResponseEntity<?> consolidatedCompetencies(  @RequestParam String companyId, @RequestParam String consolidatedAssessmentGroupName, 
           HttpSession session, @RequestParam String token) throws Exception{  
		consolidatedAssessmentGroupName = consolidatedAssessmentGroupName.trim();
		
	//	System.out.println("competency "+competency+" par "+parentCompetency+" cid "+companyId);
	 List<CompetencyAssessmentMapping> tests = competencyAssessmentMappingService.findConsolidatedAssessmentForCompetenciesByGroupName(consolidatedAssessmentGroupName, companyId);
	 return ResponseEntity.ok(tests);
	}
	
	@RequestMapping(value="allConsolidatedCompetenciesByCompany",method=RequestMethod.GET)  
    public ResponseEntity<?> allConsolidatedCompetenciesByCompany(  @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
//	 if(pageNumber == null) {
//			pageNumber = 0;
//		}
//	 Page<CompetencyAssessmentMapping> all = competencyAssessmentMappingService.findAllConsolidatedAssessmentByCompany( companyId, PageRequest.of(pageNumber, 40));
//	
//	 PaginatedResponseDto res = new PaginatedResponseDto();
//	 res.setRecordsFrom(all.getNumber());
//	 res.setRecordsTo(all.getNumberOfElements());
//	 res.setTotalNumberOfPages(all.getTotalPages());
//	 res.setSelectedPage(pageNumber + 1);
//	 res.setList(all.getContent());
//	 return ResponseEntity.ok(res);
		List<CompetencyAssessmentMapping>  all = competencyAssessmentMappingService.findAllConsolidatedAssessmentByCompanyNoPagination(companyId);
		Map<String, List<CompetencyAssessmentMapping>> map = new HashMap<>();
		for(CompetencyAssessmentMapping mapping : all) {
			if(map.get(mapping.getConsolidatedAssessmentGroupName()) == null) {
				List<CompetencyAssessmentMapping> list = new ArrayList<>();
				list.add(mapping);
				map.put(mapping.getConsolidatedAssessmentGroupName(), list);
			}
			else {
				map.get(mapping.getConsolidatedAssessmentGroupName()).add(mapping);
			}
		}
		List<ConsolidatedAssessmentDto> ret = new ArrayList<>();
		for(String groupName : map.keySet()) {
			ConsolidatedAssessmentDto dto = ConsolidatedAssessmentDto.builder().groupName(groupName)
											.list(map.get(groupName))
											.build();
			ret.add(dto);
		}
		return ResponseEntity.ok(ret);
	}
	

}
