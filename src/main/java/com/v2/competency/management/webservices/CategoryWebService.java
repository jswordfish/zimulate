package com.v2.competency.management.webservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.SubCategoryDto;
import com.v2.competency.management.entities.Category;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.Question_Source;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.entities.Test_Grading;
import com.v2.competency.management.entities.Test_Type;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.service.CategoryService;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.VFTestService;

@RestController
public class CategoryWebService {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	CompetencyService competencyService;
	
	@Autowired
	SaveVFQAService saveVFQAService;
	
	Logger logger = LoggerFactory.getLogger(CategoryWebService.class);
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping(value="fetchDistinctCategories",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchDistinctCategories( @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	List<String> categories = categoryService.findDistictCategories(companyId);
	 return ResponseEntity.ok(categories);
	}
	
	
	@RequestMapping(value="fetchSubCategories",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchSubCategories( @RequestParam String companyId, @RequestParam String category,
           HttpSession session, @RequestParam String token) throws Exception{  
	List<Category> categories = categoryService.findSubCategories(companyId, category);
	List<SubCategoryDto> subCategories = new ArrayList<>();
	Map<String, List<VFTest>> map = new HashMap<>();
	for(Category subCat : categories) {
		VFTest test = testService.findByTestName(subCat.getAssessmentName(), companyId);
		if(map.get(subCat.getSubCategory()) == null) {
			List<VFTest> tests = new ArrayList<>();
			tests.add(test);
			map.put(subCat.getSubCategory(), tests);
		}
		else {
			map.get(subCat.getSubCategory()).add(test);
		}
	}
	
	for(String subCategory : map.keySet()) {
		SubCategoryDto sub = SubCategoryDto.builder().category(category)
				.subCategory(subCategory)
				.assessments(map.get(subCategory))
				.build();
		subCategories.add(sub);
	}
	 return ResponseEntity.ok(subCategories);
	}
	
	@RequestMapping(value="uploadCategories",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadCategories( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		List<Category> categories = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, Category.class);
		System.out.println("categories zise "+categories.size() );
		System.out.println(categories.get(0).getCategory()+" - "+categories.get(0).getParentCompetency()+" - "+categories.get(0).getCompanyId());
		Map<String, List<Category>> map = new HashMap<>();
			for(Category cat : categories) {
				if(cat.getParentCompetency() == null || cat.getParentCompetency().trim().length() == 0) {
					throw new RuntimeException("Parent Competency not present");
				}
			cat.setParentCompetency(cat.getParentCompetency().trim());
			
			cat.setCompanyId(cat.getCompanyId().trim());
				if(!companyId.equals(cat.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+cat.getCompanyId());
				}
			cat.setCategory(cat.getCategory().trim());	
			cat.setSubCategory(cat.getParentCompetency());
				if(map.get(cat.getCategory()) == null) {
					List<Category> list = new ArrayList<>();
					list.add(cat);
					map.put(cat.getCategory(), list);
				}
				else {
					map.get(cat.getCategory()).add(cat);
				}
				
			}
			
		for(String category : map.keySet()) {
			System.out.println("category "+category);
			List<Category> listforCategory = map.get(category);
			for(Category subCategory : listforCategory) {
				createTests(category, subCategory.getParentCompetency(), companyId);
			}
		}
			
				
			
			 return ResponseEntity.ok("ok");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.error("Problem in saving category", e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	private void createTests(String category, String parentCompetency, String companyId) {
		try {
			List<Competency> competencies =  competencyService.findByParentCompetency(parentCompetency, companyId);
			System.out.println("in createTest finding competencues for "+parentCompetency+" cid "+companyId+" size "+competencies.size());
			for(Competency competency : competencies) {
				String assessmentName = competency.getCompetency()+" - "+category+" Assessment";
				VFTest existing = testService.findByTestName(assessmentName, companyId);
				String testIdentifier = null;
					if(existing != null) {
						testIdentifier = existing.getTestIdentifier();
					}
					else {
						testIdentifier = competency.getCompetency()+"_"+System.currentTimeMillis();
					}
				VFTest test = VFTest.builder().testIdentifier(testIdentifier)
						.duration(45)
						.gradingMethodology(Test_Grading.AI_GRADING_WITH_HUMAN_SUPERVISION.getGrading())
						.testName(assessmentName)
						.locked(true)
						.path1(category)
						.path2(parentCompetency)
						.questionSource(Question_Source.QUESTION_BANK_RANDOM.getSource())
						.testType(Test_Type.MCQ_SCENARIO.getTestType())
						.build();
				
				CompetencyTest competencyTest = CompetencyTest.builder().kbCompetencies(new ArrayList<>()).build();
				competencyTest.getKbCompetencies().add(CompetencyDto.builder().parentCompetency(parentCompetency).
						competency(competency.getCompetency()).noOfQuestionsToBeAsked(10).questionType(Question_Type.MCQ.getType()).build());
				
				competencyTest.getKbCompetencies().add(CompetencyDto.builder().parentCompetency(parentCompetency).
						competency(competency.getCompetency()).noOfQuestionsToBeAsked(2).questionType(Question_Type.SUBJECTIVE.getType()).build());
				String kbXml =  objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(competencyTest);	
				test.setCompetencyTest(competencyTest);
				test.setTestXmlForKB(kbXml);
				ResponseEntity res =   saveVFQAService.createTestKBRandomOrFixed(test, companyId, null, null);
				System.out.println("competncies dto size in test "+test.getCompetencyTest().getKbCompetencies().size()+" test saving "+res.getBody());
				createOrUpdateCategory(category, parentCompetency, competency.getCompetency(), parentCompetency, assessmentName, companyId, res.getBody().toString());
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private void createOrUpdateCategory(String category, String subCategory, String competency, String parentCompetency, String testName, String companyId, String testLink) {
		Category cat = Category.builder().assessmentName(testName)
						.category(category)
						.subCategory(subCategory)
						.parentCompetency(parentCompetency)
						.competency(competency)
						.publicTestLink(testLink)
						.build();
		
		cat.setCompanyId(companyId);
		categoryService.saveOrUpdate(cat);
	}

}
