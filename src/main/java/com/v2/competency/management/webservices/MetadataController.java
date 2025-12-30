package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.User;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.impl.PropertyConfig;
@RestController
@CrossOrigin
public class MetadataController {
	
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	PropertyConfig propertyConfig;
	
	@Autowired
	CompetencyService competencyService;
	
	@RequestMapping(value="uploadCompetencies",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadHierarchies( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<Competency> competencies = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, Competency.class);
			//System.out.println("Printing List Data: " +competencies);
			for(Competency comp : competencies) {
				 
				 try {
				 comp.setCompetency(comp.getCompetency().trim());
					if(comp.getParentCompetency() != null) {
						comp.setParentCompetency(comp.getParentCompetency().trim());
					}
					System.out.println(comp.getCompetency()+" "+comp.getParentCompetency());
					comp.setCompanyId(comp.getCompanyId().trim());
					if(!companyId.equals(comp.getCompanyId())) {
						throw new RuntimeException("Invalid Company Id "+comp.getCompanyId());
					}
				
				 comp.setCompanyId(companyId);
				 competencyService.saveOrUpdate(comp);
				 } catch (Exception e) {
						// TODO Auto-generated catch block
					 System.out.println(comp.getCompetency()+" - "+comp.getParentCompetency());
						e.printStackTrace();
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
					}
				
			}
			
			 return ResponseEntity.ok("ok");
	
 }
	
	
	@RequestMapping(value="findCompetencyByName",method=RequestMethod.GET)  
    public ResponseEntity<?> findCompetencyByName(  @RequestParam String companyId, @RequestParam String competency, 
           HttpSession session, @RequestParam String token) throws Exception{  
		Competency comp = competencyService.findByCompetency(competency, companyId);
		return ResponseEntity.ok(comp);
	}
	
	@RequestMapping(value="findByCompetencyAndParentCompetencyName",method=RequestMethod.GET)  
    public ResponseEntity<?> findByCompetencyAndParentCompetencyName(  @RequestParam String companyId, @RequestParam String competency, @RequestParam String parentCompetency,
           HttpSession session, @RequestParam String token) throws Exception{  
		Competency comp = competencyService.findByCompetencyAndParentCompetency(competency, parentCompetency, companyId);
		return ResponseEntity.ok(comp);
	}
	
	
	@RequestMapping(value="competenciesByCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> fetcCompetenciesByPage( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Competency> competencies = competencyService.getCompetenciesByCompanyId(companyId, PageRequest.of(pageNumber, 15));
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(competencies.getNumber());
	 res.setRecordsTo(competencies.getNumberOfElements());
	 res.setTotalNumberOfPages(competencies.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(competencies.getContent());
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="competenciesByLevelAndCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> fetcCompetenciesByPageForLevel( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId, @RequestParam String level,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Competency> competencies = competencyService.getCompetenciesByLevelAndCompanyId(level, companyId, PageRequest.of(pageNumber, 60));
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(competencies.getNumber());
	 res.setRecordsTo(competencies.getNumberOfElements());
	 res.setTotalNumberOfPages(competencies.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(competencies.getContent());
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="competenciesForParentCompetency",method=RequestMethod.GET)  
    public ResponseEntity<?> competenciesForParentCompetency(  @RequestParam String companyId, @RequestParam String parentCompetency,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 List<Competency> childs = competencyService.findByParentCompetency(parentCompetency, companyId);
	
	
	 return ResponseEntity.ok(childs);
	}

}
