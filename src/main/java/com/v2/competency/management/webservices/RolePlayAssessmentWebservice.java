package com.v2.competency.management.webservices;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.ComposeRequest;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.Section;
import com.googlecloud.vertex.ai.roleplay.insights.dto.newversion.SectionResult;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.common.util.PersonaCache;
import com.v2.competency.management.dtos.IndiaFirstSalesPersona;
import com.v2.competency.management.dtos.IndustryRolePlayMappingDto;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.RolePlayTestDto;
import com.v2.competency.management.dtos.SAPCustomerPersona;
import com.v2.competency.management.dtos.SCI_CustomerPersona;
import com.v2.competency.management.dtos.SalesPersona;
import com.v2.competency.management.dtos.SalesPersonaDTO;
import com.v2.competency.management.dtos.TrainerPersona;
import com.v2.competency.management.entities.RolePlayPersonaMapping;
import com.v2.competency.management.entities.RoleplayAnalysisStructure;
import com.v2.competency.management.entities.VFRolePlayTest;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.GeminiAudioVideoService;
import com.v2.competency.management.service.RolePlayPersonaMappingService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.VFRolePlayTestService;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
public class RolePlayAssessmentWebservice {
	
	@Autowired
	VFRolePlayTestService rolePlayTestService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	CompetencyService competencyService;
	
	@Autowired
	PropertyConfig propertyConfig;
	
	@Autowired
	GeminiAudioVideoService geminiAudioVideoService;
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	RolePlayPersonaMappingService rolePlayPersonaMappingService;
	
	Logger logger = LoggerFactory.getLogger(RolePlayAssessmentWebservice.class);
	
Map<String, List<String>> map = new HashMap<>();
	
	{
		String insurance[] = {"Pitch liability insurance to a skeptical small business owner.", "Upsell a bundled policy to a customer who believes they are already covered.",
				"Convince a procrastinating professional to purchase disability insurance", "Explain long-term care insurance to a retiree who is confused about its benefits", 
				"Sell a life insurance policy to a price-sensitive IT professional"};
		
		String pharma[] = {"Deliver a 2-minute pitch for a new drug to a time-crunched doctor.", "Persuade a specialist to switch from a long-used competitor's drug",
				"Convince a gatekeeper nurse to grant a meeting with the main physician", "Justify the high cost of a new drug to a hospital's budget committee",
				"Address a pharmacist's concerns about a new drug's side effects and interactions"};
		
		String realEstate[] = {"As a CSR - Align Sales Vision with the Realities of Project Delivery.",
				"Experience the entire project, from site to lifestyle, with your 24/7 Virtual Sales Concierge", 
				"Orchestrating the high-value closure for a signature prime location property",
				"Learn Prospecting, Follow-ups, Negotiation techniques, Relationship Building from a Sales Master",
				"Ask the Insider: Decoding Dubai's Ownership Laws, Rental Returns, and Resale Market."
				};
		
		String fmcg[] = {"Convince a supermarket manager to give your new product shelf space", "Announce a price increase to a key retailer without losing their business",
				"Handle an angry retailer's complaint about a popular item being out of stock", "Negotiate for better shelf placement against a competing brand.",
				"Upsell a premium product line to a small shop owner who only stocks basic items"};
		
		String banking[] = {"Sell investment products to a cautious client with large savings", "Help a small business owner with a weak plan apply for an expansion loan",
				"Upsell a credit card to a student opening their first bank account", "Retain a wealthy client who is being poached by a competitor with lower fees",
				"Qualify a freelance worker with a fluctuating income for a mortgage"};
		
		
		String automobile[] = {"Sell a 10 year old 'Used SUV car'", "Sell 'Fleet Management Solution' to Corporate Client",
				"Sell AMC services for Hyundai Cars", "Sell a Luxury Sedan to a high profile, street smart business man",
				"Sell Car Infotainment systems"};
		
		String realEstateTraining[] = {"Cold Call - Breaking the ice with potential clients to uncover hidden opportunities", "Warm Call - Connecting with interested leads to understand their vision/budget/timeline",
				"Scheduling Call - Arranging a property visit to bring a listing to life", "Negotiation - Presenting compelling offers and navigating discussions to finalize terms",
				"Nurturing Call - Staying connected with long-term prospects "};
		
		
//		String sciBasicFinancialPlanning[] = {"Financial Planning Foundations  Training for New Agents", "Financial Planning Competency Assessment for New Agents"};
//		
//		String scimysteryShopping[] = {"Evaluate 'Compliant Fact-Find' skills for Sales Agent", "Evaluate 'Explaining the Why: Basis of Recommendation' skills for Agents",
//				"Evaluate 'Clarity & Transparency: Full Product Disclosure' skills for Sales Agent", "Evaluate 'Maintaining Professional & Ethical Standards' for Sales Agent",
//				"Evaluate 'The Complete Advisory Process: A Capstone Assessment' for Sales Agent"};
//		
		String sciFidrec[] = {"Conduct 'Advisory Compliance: Vulnerable Client - Sales Process Check Training' Check for Sales Agent", 
				"Train Sales Agent on 'Advisory & Sales Process Training: Core Principles'"};
		
		
		map.put("Insurance", Arrays.asList(insurance));
		map.put("Pharma", Arrays.asList(pharma));
		map.put("Real Estate", Arrays.asList(realEstate));
		//map.put("FMCG", Arrays.asList(fmcg));
		map.put("Banking", Arrays.asList(banking));
		map.put("Automobile", Arrays.asList(automobile));
		//map.put("Real Estate Training", Arrays.asList(realEstateTraining));
		
		//map.put("SCI Basic Financial Planning", Arrays.asList(sciBasicFinancialPlanning));
		//map.put("SCI Mystery Shopping", Arrays.asList(scimysteryShopping));
		map.put("FIDREC", Arrays.asList(sciFidrec));
	}
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	String projectId = "contactaiassessments";
	Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
	
	
	private List<VFRolePlayTest> convert(List<RolePlayTestDto> dtos){
		List<VFRolePlayTest> tests = new ArrayList<>();
		for(RolePlayTestDto dto : dtos) {
			VFRolePlayTest test = new VFRolePlayTest();
			validateAnalysisParam(dto.getCommaSeparatedAnalysisParams(), test.getReportVersion());
			test.setCommaSeparatedAnalysisParams(dto.getCommaSeparatedAnalysisParams());
			test.setCompanyId(dto.getCompanyId());
			test.setDuration(dto.getDuration());
			test.setQuestionText(dto.getQuestionText());
			test.setProductInfo(dto.getProductInfo());
			test.setCompetitionInfo(dto.getCompetitionInfo());
			test.setTestName(dto.getTestName());
			RoleplayAnalysisStructure structure = RoleplayAnalysisStructure.builder()
					.analysisGenPromptEasy(dto.getAnalysisGenPromptEasy())
					.analysisGenPromptMedium(dto.getAnalysisGenPromptMedium())
					.analysisGenPromptHard(dto.getAnalysisGenPromptHard())
					.rolePlayObjective(dto.getRolePlayObjective())
					.build();
			test.setRoleplayAnalysisStructure(structure);
			test.setIndustry(dto.getIndustry());
			test.setPublished(dto.getPublished());
			test.setIsTestStartByUser(dto.getIsTestStartByUser());
			test.setDefaultQuestionPrompt(dto.getDefaultQuestionPrompt());
			test.setRolePlayLabelForUI(dto.getRolePlayLabelForUI());
			test.setAgentId(dto.getAgentId());
			test.setReportVersion(dto.getReportVersion());
			test.setAiPersona(dto.getAiPersona());
			test.setUserPersona(dto.getUserPersona());
			test.setRolePlayType(dto.getRolePlayType());
			tests.add(test);
		}
		return tests; 
	}
	
	private void validateAnalysisParam(String evalParams, String reportVersion) throws RuntimeException {
			if(reportVersion != null && reportVersion.equalsIgnoreCase("V2")) {
				String[] params = evalParams.split(System.lineSeparator());
					if(params.length == 0) {
						throw new RuntimeException("Evaluation Params Empty");
					}
					
				for(String line : params) {
					String terms[] = line.split("###");
					if(terms.length <3) {
						throw new RuntimeException("Need atleast 3 terms with 2 '###' in between. Invalid Evaluation Params "+evalParams);
					}
				}
			}
	}
	
	@RequestMapping(value="addRolePlay",method=RequestMethod.POST)  
	 public ResponseEntity<?> saveRolePlay( @RequestBody RolePlayTestDto rolePlayDto,  @RequestParam String companyId,
	           HttpSession session, @RequestParam String token) throws Exception{
		
		List<VFRolePlayTest> list = convert(Arrays.asList(rolePlayDto));
		VFRolePlayTest r = list.get(0);
		r.setTestName(r.getTestName().trim());
		r.setCompanyId(r.getCompanyId().trim());
		r.setQuestionText(r.getQuestionText().trim());
		
		if(!companyId.equals(r.getCompanyId())) {
			throw new RuntimeException("Invalid Company Id "+r.getCompanyId());
		}
		r.setCompanyId(companyId);
		
		String publicUrl = propertyConfig.getAiRolePlayTestUrl();
		publicUrl = publicUrl.replace("$[COMPANY_ID]", companyId);
		publicUrl = publicUrl.replace("$[TEST_NAME]", r.getTestName());
		r.setPublicTestLink(publicUrl);
		r = rolePlayTestService.saveOrUpdate(r)	;
		return ResponseEntity.ok(r.getId());
	 }
	
	@RequestMapping(value="checkRolePlayTestNameExists",method=RequestMethod.GET)  
    public ResponseEntity<?> checkRolePlayTestNameExists(  @RequestParam String testIdentifier,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		VFRolePlayTest test =  rolePlayTestService.findRolePlayTestsByTestName(companyId, testIdentifier);
			if(test == null) {
				return ResponseEntity.ok("Does not Exist");
			}
			else {
				return ResponseEntity.ok("Exists");
			}
		
	}
	
	@RequestMapping(value="addRolePlayPersona",method=RequestMethod.POST)  
	 public ResponseEntity<?> addRolePlayPersona( @RequestBody RolePlayPersonaMapping persona, 
	           HttpSession session, @RequestParam String token) throws Exception{
		return ResponseEntity.ok(rolePlayPersonaMappingService.addRolePlayPersonaMapping(persona));
	}
	
	@RequestMapping(value="updateRolePlayPersona",method=RequestMethod.POST)  
	 public ResponseEntity<?> updateRolePlayPersona( @RequestBody RolePlayPersonaMapping persona, 
	           HttpSession session, @RequestParam String token) throws Exception{
		return ResponseEntity.ok(rolePlayPersonaMappingService.updateRolePlayPersonaMapping(persona));
	}
	
	@RequestMapping(value="uploadRolePlayTests",method=RequestMethod.POST)  
    public ResponseEntity<?> uploadRoles( @RequestParam MultipartFile file,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 
	 try {
		 
		 if(!tenantService.isCompanyIdExisting(companyId)) {
			 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
		 }
		 
		 List<RolePlayTestDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, RolePlayTestDto.class);
		 List<VFRolePlayTest> tests = convert(dtos);
			for(VFRolePlayTest r : tests) {
				r.setTestName(r.getTestName().trim());
				r.setCompanyId(r.getCompanyId().trim());
				r.setQuestionText(r.getQuestionText().trim());
				
				if(!companyId.equals(r.getCompanyId())) {
					throw new RuntimeException("Invalid Company Id "+r.getCompanyId());
				}
				r.setCompanyId(companyId);
				
				String publicUrl = propertyConfig.getAiRolePlayTestUrl();
				publicUrl = publicUrl.replace("$[COMPANY_ID]", companyId);
				publicUrl = publicUrl.replace("$[TEST_NAME]", r.getTestName());
				r.setPublicTestLink(publicUrl);
				rolePlayTestService.saveOrUpdate(r)	;
				
			}
			 return ResponseEntity.ok("ok");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.error("Problem in saving role", e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
	}
 }
	
	@RequestMapping(value="fetchRolePlayassessmentsByCompetency",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchRolePlayassessmentsByCompetency(  @RequestParam String competency,@RequestParam String parentCompetency,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	
	 return ResponseEntity.ok(rolePlayTestService.findRolePlayTestsForCompetency(competency, parentCompetency, companyId));
	}
	
	
	@RequestMapping(value="fetchRolePlayassessmentsByCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> fetcCompetenciesByPageForLevel( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<VFRolePlayTest> tests = rolePlayTestService.getRolePlayTestsByCompanyId(companyId, PageRequest.of(pageNumber, 35));
	 
	
	 	
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(tests.getNumber());
	 res.setRecordsTo(tests.getNumberOfElements());
	 res.setTotalNumberOfPages(tests.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 res.setList(tests.getContent());
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value = "fetchTrainingRolePlays", method = RequestMethod.GET)
	public ResponseEntity<?> fetchTrainingRolePlays(
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpSession session) throws Exception {

	    PaginatedResponseDto paginatedResponse =
	            rolePlayTestService.searchTrainingRolePlays(
	                    companyId,
	                    search,
	                    page,
	                    size
	            );

	    return ResponseEntity.ok(paginatedResponse);
	}
	
	@RequestMapping(value = "fetchAssessmentRolePlays", method = RequestMethod.GET)
	public ResponseEntity<?> fetchAssessmentRolePlays(
	        @RequestParam String companyId,
	        @RequestParam String token,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        HttpSession session) throws Exception {

	    PaginatedResponseDto paginatedResponse =
	            rolePlayTestService.searchAssessmentRolePlays(
	                    companyId,
	                    search,
	                    page,
	                    size
	            );

	    return ResponseEntity.ok(paginatedResponse);
	}
	
	
	@RequestMapping(value="industryBasedRoleplays",method=RequestMethod.GET)  
    public ResponseEntity<List<IndustryRolePlayMappingDto>> industryBasedRoleplays( @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{ 
		List<VFRolePlayTest> tests = rolePlayTestService.findPublishedTests(companyId);
		
		List<RolePlayTestDto> testDtos = new ArrayList<>();
		for(VFRolePlayTest test : tests) {
			RolePlayTestDto dto = new RolePlayTestDto();
			mapper.map(test, dto);
			testDtos.add(dto);
		}
		
		List<IndustryRolePlayMappingDto> industries =   testDtos.stream()
			.collect(Collectors.groupingBy(RolePlayTestDto::getIndustry))
			.entrySet().stream()
			.map(entry -> new IndustryRolePlayMappingDto(entry.getKey(), entry.getValue()))
			.collect(Collectors.toList());
		
		
		
		/**
		 * Step 1 - make sure general industry has 5 role plays
		 */
		List<RolePlayTestDto> generalTests = null;
			if(tests.size() > 4) {
				generalTests = testDtos.subList(0, 5);
			}
			else {
				generalTests = new ArrayList<>();
				generalTests.addAll(testDtos);
				List<Integer> numbers = new ArrayList<>();
				for(int i=tests.size(); i<5; i++) {
					numbers.add(i);
				}
				int count = 0;
				
				for(int i= tests.size(); i < 5; i++) {
					RolePlayTestDto dummy = new RolePlayTestDto();
					generalTests.add(dummy);
					if(count == 0) {
						dummy.setTestName("Dummy - "+map.get("Pharma").get(i));
						dummy.setRolePlayLabelForUI(map.get("Pharma").get(i));
					}
					else if(count ==1) {
						dummy.setTestName("Dummy - "+map.get("Insurance").get(i));
						dummy.setRolePlayLabelForUI(map.get("Insurance").get(i));
					}
					else if(count ==2) {
						dummy.setTestName("Dummy - "+map.get("Real Estate").get(i));
						dummy.setRolePlayLabelForUI(map.get("Real Estate").get(i));
					}
					else if(count ==3) {
						dummy.setTestName("Dummy - "+map.get("Banking").get(i));
						dummy.setRolePlayLabelForUI(map.get("Banking").get(i));
					}
					else{
						dummy.setTestName("Dummy - "+map.get("FMCG").get(i));
						dummy.setRolePlayLabelForUI(map.get("FMCG").get(i));
					}
					count++;
				}
			}
		//IndustryRolePlayMappingDto general = new IndustryRolePlayMappingDto("General", generalTests);	
		//industries.add(0, general);
		

		
		List<RolePlayTestDto> sciFidrecTests = new ArrayList<>();
		IndustryRolePlayMappingDto sciFidrec = new IndustryRolePlayMappingDto("FIDREC", sciFidrecTests);	
		industries.add(sciFidrec);
		
		
		
		/**
		 * Step 2 - Make sure all other categories have 5 tests
		 */
		for(IndustryRolePlayMappingDto industry : industries) {
			List<RolePlayTestDto> industryTests = null;
			List<RolePlayTestDto> actualTestsFromDb = industry.getRoleplays();
			if(actualTestsFromDb.size() > 4) {
				industryTests = actualTestsFromDb.subList(0, 5);
			}
			else {
				industryTests = new ArrayList<>();
				industryTests.addAll(actualTestsFromDb);
				
				for(int i=actualTestsFromDb.size(); i<5; i++) {
					
					if(industry.getIndustry().equals("Insurance")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Insurance").get(i));
						dummy.setRolePlayLabelForUI(map.get("Insurance").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("Banking")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Banking").get(i));
						dummy.setRolePlayLabelForUI(map.get("Banking").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("Pharma")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Pharma").get(i));
						dummy.setRolePlayLabelForUI(map.get("Pharma").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("FMCG")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("FMCG").get(i));
						dummy.setRolePlayLabelForUI(map.get("FMCG").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("Real Estate")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Real Estate").get(i));
						dummy.setRolePlayLabelForUI(map.get("Real Estate").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("Automobile")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Automobile").get(i));
						dummy.setRolePlayLabelForUI(map.get("Automobile").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
					else if(industry.getIndustry().equals("Real Estate Training")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
						dummy.setTestName("Dummy - "+map.get("Real Estate Training").get(i));
						dummy.setRolePlayLabelForUI(map.get("Real Estate Training").get(i));
						industryTests.add(dummy);
						industry.setRoleplays(industryTests);
					}
//					else if(industry.getIndustry().equals("SCI Basic Financial Planning")) {
//						RolePlayTestDto dummy = new RolePlayTestDto();
//							if(i < map.get("SCI Basic Financial Planning").size() ) {
//								dummy.setTestName("Dummy - "+map.get("SCI Basic Financial Planning").get(i));
//								dummy.setRolePlayLabelForUI(map.get("SCI Basic Financial Planning").get(i));
//								industryTests.add(dummy);
//								industry.setRoleplays(industryTests);
//							}
//					}
//					else if(industry.getIndustry().equals("SCI Mystery Shopping")) {
//						RolePlayTestDto dummy = new RolePlayTestDto();
//							if(i < map.get("SCI Mystery Shopping").size() ) {
//								dummy.setTestName("Dummy - "+map.get("SCI Mystery Shopping").get(i));
//								dummy.setRolePlayLabelForUI(map.get("SCI Mystery Shopping").get(i));
//								industryTests.add(dummy);
//								industry.setRoleplays(industryTests);
//							}
//					}
					else if(industry.getIndustry().equals("FIDREC")) {
						RolePlayTestDto dummy = new RolePlayTestDto();
							if(i < map.get("FIDREC").size() ) {
								dummy.setTestName("Dummy - "+map.get("FIDREC").get(i));
								dummy.setRolePlayLabelForUI(map.get("FIDREC").get(i));
								industryTests.add(dummy);
								industry.setRoleplays(industryTests);
							}
					}
					
					
					
				}
			}
		}
		
		/**
		 * Lets make sure we have sufficient industry-tests available to display on Ui
		 * If not, lets create those industries. Key industries present should be - 
		 * 'Insurance', 'Pharma', 'Real Estate', 'FMCG', 'Banking'
		 */
		if(!industries.contains(new IndustryRolePlayMappingDto("Insurance"))) {
			List<RolePlayTestDto> insuranceRoleplays = getDummyRoleplays(new ArrayList<>(), "Insurance");
			IndustryRolePlayMappingDto insurance = new IndustryRolePlayMappingDto("Insurance", insuranceRoleplays);
			industries.add(1, insurance);
		}
		
		if(!industries.contains(new IndustryRolePlayMappingDto("Pharma"))) {
			List<RolePlayTestDto> pharmaRoleplays = getDummyRoleplays(new ArrayList<>(), "Pharma");
			IndustryRolePlayMappingDto pharma = new IndustryRolePlayMappingDto("Pharma", pharmaRoleplays);
			industries.add(2, pharma);
		}
		
		if(!industries.contains(new IndustryRolePlayMappingDto("Real Estate"))) {
			List<RolePlayTestDto> realEstateRoleplays = getDummyRoleplays(new ArrayList<>(), "Real Estate");
			IndustryRolePlayMappingDto realEstate = new IndustryRolePlayMappingDto("Real Estate", realEstateRoleplays);
			industries.add(3, realEstate);
		}
		
//		if(!industries.contains(new IndustryRolePlayMappingDto("FMCG"))) {
//			List<RolePlayTestDto> fmcgRoleplays = getDummyRoleplays(new ArrayList<>(), "FMCG");
//			IndustryRolePlayMappingDto fmcg = new IndustryRolePlayMappingDto("FMCG", fmcgRoleplays);
//			industries.add(4, fmcg);
//		}
		
		if(!industries.contains(new IndustryRolePlayMappingDto("Banking"))) {
			List<RolePlayTestDto> bankingRoleplays = getDummyRoleplays(new ArrayList<>(), "Banking");
			IndustryRolePlayMappingDto insurance = new IndustryRolePlayMappingDto("Banking", bankingRoleplays);
			industries.add(5, insurance);
		}
		
		
		return ResponseEntity.ok(industries);
			
	}
	
	private List<RolePlayTestDto> getDummyRoleplays(List<RolePlayTestDto> tests, String industry){
		List<RolePlayTestDto> industryTests = new ArrayList<>();
		industryTests.addAll(tests);
		for(int i=tests.size(); i<5; i++) {
			RolePlayTestDto dummy = new RolePlayTestDto();
			if(industry.equals("Insurance")) {
				dummy.setTestName("Dummy - "+map.get("Insurance").get(i));
				dummy.setRolePlayLabelForUI(map.get("Insurance").get(i));
			}
			else if(industry.equals("Banking")) {
				dummy.setTestName("Dummy - "+map.get("Banking").get(i));
				dummy.setRolePlayLabelForUI(map.get("Banking").get(i));
			}
			else if(industry.equals("Pharma")) {
				dummy.setTestName("Dummy - "+map.get("Pharma").get(i));
				dummy.setRolePlayLabelForUI(map.get("Pharma").get(i));
			}
			else if(industry.equals("FMCG")) {
				dummy.setTestName("Dummy - "+map.get("FMCG").get(i));
				dummy.setRolePlayLabelForUI(map.get("FMCG").get(i));
			}
			else if(industry.equals("Real Estate")) {
				dummy.setTestName("Dummy - "+map.get("Real Estate").get(i));
				dummy.setRolePlayLabelForUI(map.get("Real Estate").get(i));
			}
			else if(industry.equals("Automobile")) {
				dummy.setTestName("Dummy - "+map.get("Automobile").get(i));
				dummy.setRolePlayLabelForUI(map.get("Automobile").get(i));
			}
			else {
				dummy.setTestName("Dummy - "+map.get("Banking").get(i));
				dummy.setRolePlayLabelForUI(map.get("Banking").get(i));
				
			}
			
			industryTests.add(dummy);
		}
		return industryTests;
		
		//
	}
	
	@RequestMapping(value="createFolderStructureForRoleplayVideo",method=RequestMethod.POST)  
    public ResponseEntity<?> createFolderStructureForRoleplayVideo(  @RequestParam String companyId, @RequestParam String rolePlayTestName, 
    		@RequestParam String email, @RequestParam Integer attempt, @RequestParam String date, @RequestParam String fileName,
           HttpSession session, @RequestParam String token) throws Exception{  
		createFolders(companyId, rolePlayTestName, date, email, attempt);
		String fileNameWithPath = companyId+"/"+rolePlayTestName+"/"+date+"/"+email+"/"+attempt+"/"+fileName;
		URL url = generateV4PutSignedUrl(fileNameWithPath);
		return ResponseEntity.ok(url.toString());
	}
	
	private void createFolders(Object ...objects ) {
		String folderPath = "";
		for(int i=0;i<objects.length;i++) {
			folderPath += objects[i].toString()+"/";
			createIfNeededFolder(folderPath);
		}
		
	}
	
	private void createIfNeededFolder(String folder) {
		BlobId blobId = BlobId.of("zimulate", folder);
	    
	    Blob blob = storage.get(blobId);
	    if(blob != null && blob.exists()) {
	    	return;
	    }
	    else {
	    	BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, new byte[0]);
	    }
	}
	
	
	private  URL generateV4PutSignedUrl( String objectNameWithCompleteFolder)
	        throws StorageException {

	        // 1. Initialize the Storage client
	        // This authenticates using Application Default Credentials.
	        // Ensure the service account has the "Service Account Token Creator" role.
	       

	        // 2. Define the BlobInfo for the object to be uploaded.
	        // This includes the bucket name and the full object path (folder + filename).
		BlobId blobId = BlobId.of("zimulate", objectNameWithCompleteFolder);
	        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

	        // 3. Set the duration for which the signed URL will be valid.
	        long duration = 60; // 60 minutes

	        // 4. Generate the V4 signed URL with the PUT method to allow uploads.
	        // The URL will be valid for the specified duration.
	        URL signedUrl =
	            storage.signUrl(
	                blobInfo,
	                duration,
	                TimeUnit.MINUTES,
	                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
	                Storage.SignUrlOption.withV4Signature()
	            );
	       
	        return signedUrl;
	    }
	
	
	@RequestMapping(value="analyseGoogleCloudBucketVideo",method=RequestMethod.POST)  
    public ResponseEntity<?> analyseGoogleCloudBucketVideo(@RequestParam String prompt,  @RequestParam String googleCloudBuckerUrl,
           HttpSession session, @RequestParam String token) throws Exception{  
		String aiResponse = geminiAudioVideoService.videoInputWithGoogleCloudBucketUrl(config.getGeminiLocation(), config.getGeminiModelName(), prompt, googleCloudBuckerUrl);
		return ResponseEntity.ok(aiResponse);
	}
	
	@RequestMapping(value="mergeGoogleCloudBucketVideo",method=RequestMethod.POST)  
    public ResponseEntity<?> mergeGoogleCloudBucketVideo(@RequestParam String targetFileWithPath,  
           HttpSession session, @RequestParam String token, @RequestBody List<String> orderedVideosWithPath) throws Exception{  
		combineObjects("zimulate", orderedVideosWithPath, targetFileWithPath);
		return ResponseEntity.ok("ok");
	}
	
	public static void combineObjects(
		      String bucketName, List<String> sourceObjectNames, String destinationObjectName) {

		    try {
		      // The client automatically uses Application Default Credentials.
		      Storage storage = StorageOptions.getDefaultInstance().getService();

		      // Define the destination object's ID.
		      BlobId destinationBlobId = BlobId.of(bucketName, destinationObjectName);
		      BlobInfo destBlobInfo = BlobInfo.newBuilder(destinationBlobId).build();

		      // Build the compose request from the source object names.
		      ComposeRequest composeRequest =
		          ComposeRequest.newBuilder()
		              .setTarget(destBlobInfo)
		              .addSource(sourceObjectNames)
		              .build();

		      // Execute the compose request.
		      storage.compose(composeRequest);

		      System.out.println(
		          "Successfully combined "
		              + sourceObjectNames.size()
		              + " files into gs://"
		              + bucketName
		              + "/"
		              + destinationObjectName);

		    } catch (StorageException e) {
		      System.err.println("Error combining objects: " + e.getMessage());
		      System.err.println("Error code: " + e.getCode());
		    } catch (Exception e) {
		      System.err.println("An unexpected error occurred: " + e.getMessage());
		    }
		  }
	
	
	@RequestMapping(value="personasBasedOnTypes",method=RequestMethod.GET)  
    public ResponseEntity<?> personasBasedOnTypes(
           HttpSession session, @RequestParam String token, @RequestParam String type) throws Exception{  
		return ResponseEntity.ok(PersonaCache.getPersonas(type));
	}
	//
	@RequestMapping(value="personasBasedOnTypesAndCompanyId",method=RequestMethod.GET)  
    public ResponseEntity<?> personasBasedOnTypesAndCompanyId(
           HttpSession session, @RequestParam String token, @RequestParam String type, @RequestParam String companyId) throws Exception{  
		List<RolePlayPersonaMapping> list =  rolePlayPersonaMappingService.findPersonasForTypeAndCompanyId(type, companyId);
		List<SalesPersonaDTO> list2 = new ArrayList<>();
			for(RolePlayPersonaMapping mapping : list) {
				SalesPersonaDTO dto = SalesPersonaDTO.builder().persona(mapping.getPersona())
										.personaDesc(mapping.getPersonaDescription())
										.voiceId(mapping.getVoiceId())
										.build();
				list2.add(dto);
			}
		if(list2.size() == 0) {
			return ResponseEntity.ok(PersonaCache.getPersonas(type));
		}
		return ResponseEntity.ok(list2);
	}
	
	@RequestMapping(value="salesPersonas",method=RequestMethod.GET)  
    public ResponseEntity<?> salesPersonas(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(SalesPersona.Bargain_Hunter.getAllDtos());
	}
	
	@RequestMapping(value="sciBasicFinancialPlanning",method=RequestMethod.GET)  
    public ResponseEntity<?> sciBasicFinancialPlanning(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(TrainerPersona.The_Relatable_StoryTeller.getAllDtos());
	}
	
	@RequestMapping(value="sciMysteryshopping",method=RequestMethod.GET)  
    public ResponseEntity<?> sciMysteryshopping(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(SCI_CustomerPersona.Community_Focused_Investor.getAllDtos());
	}
	
	@RequestMapping(value="getIndiaFirstTrainerPersonas",method=RequestMethod.GET)  
    public ResponseEntity<?> getIndiaFirstTrainerPersonas(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(TrainerPersona.The_Relatable_StoryTeller.getAllDtos());
	}
	
	@RequestMapping(value="getIndiaFirstSalesPersonas",method=RequestMethod.GET)  
    public ResponseEntity<?> getIndiaFirstSalesPersonas(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(IndiaFirstSalesPersona.The_Chameleon.getAllDtos());
	}
	
	@RequestMapping(value="getSAPCustomerPersonas",method=RequestMethod.GET)  
    public ResponseEntity<?> getSAPCustomerPersonas(
           HttpSession session, @RequestParam String token) throws Exception{  
		return ResponseEntity.ok(SAPCustomerPersona.CFO.getAllDtos());
	}

}
