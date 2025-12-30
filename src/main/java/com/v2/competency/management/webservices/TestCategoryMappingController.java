package com.v2.competency.management.webservices;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.TestCategoryMapping;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.TestCategoryMappingService;

@RestController
@CrossOrigin
public class TestCategoryMappingController {
	@Autowired
	TenantService tenantService;
	
	@Autowired
	TestCategoryMappingService testCategoryMappingService;
	
	
	@RequestMapping(value="uploadTestCategories",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadCategories( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
		 try {
			 
			 if(!tenantService.isCompanyIdExisting(companyId)) {
				 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
			 }
			 
			List<TestCategoryMapping> mappings = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, TestCategoryMapping.class);
				//System.out.println("Printing List Data: " +mappings);
				for(TestCategoryMapping map : mappings) {
					
					map.setTestName(map.getTestName().trim());
					map.setTestIdentifier(map.getTestIdentifier().trim());
					map.setCategory(map.getCategory().trim());
						if(map.getSubCategory() != null) {
							map.setSubCategory(map.getSubCategory().trim());
						}
						
						if(map.getSubSubCategory() != null) {
							map.setSubSubCategory(map.getSubSubCategory().trim());
						}
					
					if(!companyId.equals(map.getCompanyId())) {
						throw new RuntimeException("Invalid Company Id "+map.getCompanyId());
					}
					
					map.setCompanyId(companyId);
					testCategoryMappingService.saveOrUpdate(map);
					
				}
				
				 return ResponseEntity.ok("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
		}
	}
	
	
	@RequestMapping(value="findUniqueTestCategoryMapping",method=RequestMethod.GET)  
    public ResponseEntity<?> findUniqueTestCategoryMapping(  @RequestParam String companyId, @RequestParam String category,  @RequestParam String subCategory,  @RequestParam String subSubCategory, @RequestParam String testIdentifier, 
           HttpSession session, @RequestParam String token) throws Exception{  
		TestCategoryMapping testCategoryMapping =   testCategoryMappingService.findByPrimaryKey(companyId, category, subCategory, subSubCategory, testIdentifier);
		return ResponseEntity.ok(testCategoryMapping);
	}
	
	@RequestMapping(value="findTestsBySubSubCategory",method=RequestMethod.GET)  
    public ResponseEntity<?> findTestsBySubSubCategory(  @RequestParam String companyId, @RequestParam String category,  @RequestParam String subCategory,  @RequestParam String subSubCategory, 
           HttpSession session, @RequestParam String token) throws Exception{  
		List<TestCategoryMapping> list =   testCategoryMappingService.findTestsBySubSubCategory(companyId, category, subCategory, subSubCategory);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="findTestsBySubCategory",method=RequestMethod.GET)  
    public ResponseEntity<?> findTestsBySubCategory(  @RequestParam String companyId, @RequestParam String category,  @RequestParam String subCategory,  
           HttpSession session, @RequestParam String token) throws Exception{  
		List<TestCategoryMapping> list =   testCategoryMappingService.findTestsBySubCategory(companyId, category, subCategory);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="findTestsByCategory",method=RequestMethod.GET)  
    public ResponseEntity<?> findTestsByCategory(  @RequestParam String companyId, @RequestParam String category, 
           HttpSession session, @RequestParam String token) throws Exception{  
		List<TestCategoryMapping> list =   testCategoryMappingService.findTestsByCategory(companyId, category);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="findDistinctCategories",method=RequestMethod.GET)  
    public ResponseEntity<?> findDistinctCategories(  @RequestParam String companyId, 
           HttpSession session, @RequestParam String token) throws Exception{  
		List<String> list =   testCategoryMappingService.findDistinctCategories(companyId);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="findSubCategories",method=RequestMethod.GET)  
    public ResponseEntity<?> findSubCategories(  @RequestParam String companyId, @RequestParam String category, 
           HttpSession session, @RequestParam String token) throws Exception{  
		List<String> list =   testCategoryMappingService.findSubCategories(companyId, category);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="findSubSubCategories",method=RequestMethod.GET)  
    public ResponseEntity<?> findSubSubCategories(  @RequestParam String companyId, @RequestParam String category,  @RequestParam String subCategory,  @RequestParam String subSubCategory, 
           HttpSession session, @RequestParam String token) throws Exception{  
		List<String> list =   testCategoryMappingService.findSubSubCategories(companyId, category, subCategory);
		return ResponseEntity.ok(list);
	}
	
	@RequestMapping(value="saveTestCategoryMapping",method=RequestMethod.POST)  
    public ResponseEntity<?> saveTestCategoryMapping( @RequestBody TestCategoryMapping mapping,
           HttpSession session, @RequestParam String token) throws Exception{ 
		return  ResponseEntity.ok(testCategoryMappingService.saveOrUpdate(mapping));
	}

}
