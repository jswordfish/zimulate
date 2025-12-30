package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.List;

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
import com.v2.competency.management.entities.VerticalCompetency;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.VerticalCompetencyService;
@RestController
@CrossOrigin
public class VerticalController {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	VerticalCompetencyService verticalCompetencyService;
	
	
	@RequestMapping(value="uploadVerticals",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadVerticals( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<VerticalCompetency> competencies = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, VerticalCompetency.class);
			System.out.println("Printing List Data: " +competencies);
			for(VerticalCompetency comp : competencies) {
				comp.setVertical(comp.getVertical().trim());
				comp.setCompetency(comp.getCompetency().trim());
				if(!companyId.equals(comp.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+comp.getCompanyId());
				}
				
				comp.setCompanyId(companyId);
				verticalCompetencyService.saveOrUpdate(comp);
				
			}
			
			 return ResponseEntity.ok("ok");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	
	@RequestMapping(value="verticalsByCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> verticalsByCompanyId(  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 List<String> verticals = verticalCompetencyService.findVerticalsForCompanyId(companyId);
	 return ResponseEntity.ok(verticals);
	}
	
	
	@RequestMapping(value="competenciesForVerticals",method=RequestMethod.GET)  
    public ResponseEntity<?> competenciesForVerticals(  @RequestParam String companyId,  @RequestParam String vertical,
           HttpSession session, @RequestParam String token) throws Exception{  
	 List<VerticalCompetency> competencies = verticalCompetencyService.findCompetenciesForVertical(vertical, companyId);
	 return ResponseEntity.ok(competencies);
	}

}
