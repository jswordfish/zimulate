package com.v2.competency.management.webservices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.v2.competency.management.dtos.PaginatedResponseDto;
import com.v2.competency.management.dtos.QuestionAvailabilityCountDto;
import com.v2.competency.management.dtos.QuestionDto;
import com.v2.competency.management.entities.Competency;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;
import com.v2.competency.management.service.CompetencyService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.TenantService;
import com.v2.competency.management.service.impl.PropertyConfig;
import com.voice.avatar.platform.resemble.dto.Response;
import com.voice.avatar.platform.resemble.util.ResembleUtil;
@RestController
@CrossOrigin
public class QuestionWebService {
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	CompetencyService competencyService;
	
	@Autowired
	PropertyConfig config;
	
//	Question findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(String questionText, String competency, String parentCompetency, String companyId);
//	 Page<Question> findAllQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
//	 Page<Question> findPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
//	 Page<Question> findUnPublishedQuestionsByCompetencyForCompany(@Param("competency")  String competency, @Param("parentCompetency")  String parentCompetency,   @Param("companyId")  String companyId, Pageable pageable);
//	 Question saveOrUpdate(Question question); 
//	 void deleteQuestion(Long id); 
//	 void publishQuestion(Long id);
//	 void unPublishQuestion(Long id); 
//	 List<Question> fetchQuestions(String prompt, String competency, String parentCompetency);
//	 

		ObjectMapper objectMapper = new ObjectMapper();
		
		Mapper mapper = DozerBeanMapperBuilder.buildDefault();
		
	private List<QuestionDto> convert(List<Question> questions){
		List<QuestionDto> qs = new ArrayList<>();
			for(Question q : questions) {
				QuestionDto q1 = new QuestionDto();
				mapper.map(q, q1);
				q1.setQid(q.getId());
				qs.add(q1);
			}
		return qs;
	}
	
	
	@RequestMapping(value="fetchAllQuestionsByCompetency",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchAllQuestionsByCompetency( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Question> qs = questionService.findAllQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, PageRequest.of(pageNumber, 15));
	 List<Question> questions =  qs.getContent();
	
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(qs.getNumber());
	 res.setRecordsTo(qs.getNumberOfElements());
	 res.setTotalNumberOfPages(qs.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 List<QuestionDto> list = convert(questions);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="fetchPublishedQuestionsByCompetency",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchPublishedQuestionsByCompetency( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam(required = false) String questionType,  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Question> qs = null;
	 	if(questionType == null) {
	 		qs = questionService.findPublishedQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, PageRequest.of(pageNumber, 15));
	 	}
	 	else {
	 		qs = questionService.findPublishedQuestionsByCompetencyAndQuestionTypeForCompany(questionType, competency, parentCompetency, companyId, PageRequest.of(pageNumber, 15));
	 	}
	 String qType = questionType != null ?questionType:Question_Type.MCQ.getType();
	List<Question> questionsWithMultipleCompetencies = questionService.findAllQuestionsAssociatedWithMultipleCompetenciesByCompetencyForCompany(qType, competency, parentCompetency, companyId) ;	
	 List<Question> result = new ArrayList<>();
	 List<Question> questions =  qs.getContent();
	 
	 result.addAll(questions);
	 result.addAll(questionsWithMultipleCompetencies);
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(qs.getNumber());
	 res.setRecordsTo(qs.getNumberOfElements());
	 res.setTotalNumberOfPages(qs.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 List<QuestionDto> list = convert(result);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	@RequestMapping(value="getQuestionAvailabilityCount",method=RequestMethod.GET)  
    public ResponseEntity<?> getQuestionAvailabilityCount(@RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		
		List<QuestionAvailabilityCountDto> list = questionService.findQuestionAvailabilityCount(competency, parentCompetency, companyId);
			return ResponseEntity.ok(list);
	}
	
	
	
	@RequestMapping(value="fetchUnPublishedQuestionsByCompetency",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchUnPublishedQuestionsByCompetency( @RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
	 if(pageNumber == null) {
			pageNumber = 0;
		}
	 Page<Question> qs = questionService.findUnPublishedQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, PageRequest.of(pageNumber, 15));
	 List<Question> questions =  qs.getContent();
	 PaginatedResponseDto res = new PaginatedResponseDto();
	 res.setRecordsFrom(qs.getNumber());
	 res.setRecordsTo(qs.getNumberOfElements());
	 res.setTotalNumberOfPages(qs.getTotalPages());
	 res.setSelectedPage(pageNumber + 1);
	 List<QuestionDto> list = convert(questions);
	 res.setList(list);
	 return ResponseEntity.ok(res);
	}
	
	
	@RequestMapping(value="saveOrUpdateQuestion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> saveOrUpdateQuestion(@RequestBody Question question,  @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		Objects.requireNonNull(question.getCompanyId());
		Objects.requireNonNull(question.getQuestionText());
		Objects.requireNonNull(question.getQuestionType());
		if(question.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
			Objects.requireNonNull(question.getChoice1());
			Objects.requireNonNull(question.getChoice2());
			Objects.requireNonNull(question.getRightChoice());
		}
		questionService.saveOrUpdate(question);
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="deleteQuestion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> deleteQuestion(  @RequestParam Long questionId,
           HttpSession session, @RequestParam String token) throws Exception{  
		questionService.deleteQuestion(questionId);
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="deleteQuestionList",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> deleteQuestionList(  @RequestParam Long[] questionIds,
           HttpSession session, @RequestParam String token) throws Exception{  
		
		for(Long id : questionIds) {
			questionService.deleteQuestion(id);
		}
		
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="publishQuestion",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> publishQuestion(  @RequestParam Long questionId,
           HttpSession session, @RequestParam String token) throws Exception{  
		questionService.publishQuestion(questionId);
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="publishQuestionList",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> publishQuestionList(  @RequestParam Long[] questionIds,
           HttpSession session, @RequestParam String token) throws Exception{  
		for(Long id : questionIds) {
			questionService.publishQuestion(id);
		}
		
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="unpublishQuestion",method=RequestMethod.POST) 
	@CrossOrigin
    public ResponseEntity<?> unpublishQuestion(  @RequestParam Long questionId,
           HttpSession session, @RequestParam String token) throws Exception{  
		questionService.unPublishQuestion(questionId);
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="unPublishQuestionList",method=RequestMethod.POST)  
	@CrossOrigin
    public ResponseEntity<?> unPublishQuestionList(  @RequestParam Long[] questionIds,
           HttpSession session, @RequestParam String token) throws Exception{  
		for(Long id : questionIds) {
			questionService.unPublishQuestion(id);
		}
		
		return ResponseEntity.ok("ok");
	}
	
	@RequestMapping(value="getDefaultPromptForFetchingMCQQuestions",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> getDefaultPromptForFetchingMCQQuestions(  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		String prompt = "For a competency "+parentCompetency+" and its sub competency "+competency+", please fetch a list of  10 unique & difficult MCQ questions. "+
           "Each question should have 4 choices with 1 correct choice. ";
		Question q = Question.builder().questionText("Sample Question")
				.choice1("Abc")
				.choice2("Def")
				.choice3("Pqr")
				.choice4("Xyz")
				.rightChoice("Choice 1")
				.imageUrl("If applicable share a relevant image URL")
				.build();
		List<Question> list = Arrays.asList(q);
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
		prompt+= "Correct Choice should be returned striuctly as either of Choice 1 or Choice 2 or Choice 3 or Choice 4. "+
		"Return your response in json format below \n"+json;
		return ResponseEntity.ok(prompt);
		
	}
	
	@RequestMapping(value="getDefaultPromptForFetchingSurveyQuestions",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> getDefaultPromptForFetchingSurveyQuestions(  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		String prompt = "For a competency "+parentCompetency+" and its sub competency "+competency+" we need to conduct a survey. please fetch a list of  10 unique & difficult Survey questions. "+
           "Each question should have 4 choices. There is no need to have any choice as correct. ";
		Question q = Question.builder().questionText("Sample Question")
				.choice1("Sample Choice 1")
				.choice2("Sample Choice 2")
				.choice3("Sample Choice 3")
				.choice4("Sample Choice 4")
				.rightChoice("Not needed")
				.imageUrl("If applicable share a relevant image URL")
				.build();
		List<Question> list = Arrays.asList(q);
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
		prompt+= ""+
		"Return your response in json format below \n"+json;
		return ResponseEntity.ok(prompt);
		
	}
	
	@RequestMapping(value="getDefaultPromptForFetchingSubjectiveQuestions",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> getDefaultPromptForFetchingSubjectiveQuestions(  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		String prompt = "For a competency "+parentCompetency+" and its sub competency "+competency+", please fetch a list of  10 unique & difficult scenario based questions. ";
		List<String> questions = new ArrayList<>();
		questions.add("Question 1..");
		questions.add("Question 2..");
		String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(questions);
		prompt+= "Return your response as a list of strings in json format below \n"+json;
				
		return ResponseEntity.ok(prompt);
	}
	
	@RequestMapping(value="fetchMCQQuestionsFromAIAndSaveUnPublished",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchQuestionsFromAI( @RequestParam String prompt,  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		
		List<Question> questions =   questionService.fetchMCQQuestions(prompt, competency, parentCompetency);
			for(Question qs : questions) {
				qs.setCompanyId(companyId);
				qs.setCompetency(competency);
				qs.setParentCompetency(parentCompetency);
				qs.setQuestionType(Question_Type.MCQ.getType());
				qs.setPublished(false);
				questionService.saveOrUpdate(qs);
			}
			
		return ResponseEntity.ok(questions);
	}
	
	@RequestMapping(value="fetchSurveyQuestionsFromAIAndSaveUnPublished",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchSurveyQuestionsFromAIAndSaveUnPublished( @RequestParam String prompt,  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		
		List<Question> questions =   questionService.fetchMCQQuestions(prompt, competency, parentCompetency);
			for(Question qs : questions) {
				qs.setCompanyId(companyId);
				qs.setCompetency(competency);
				qs.setParentCompetency(parentCompetency);
				qs.setQuestionType(Question_Type.SURVEY.getType());
				qs.setPublished(false);
				questionService.saveOrUpdate(qs);
			}
			
		return ResponseEntity.ok(questions);
	}
	
	@RequestMapping(value="fetchTextBasedQuestionsFromAIAndSaveUnPublished",method=RequestMethod.GET)  
	@CrossOrigin
    public ResponseEntity<?> fetchTextBasedQuestionsFromAI( @RequestParam String prompt,  @RequestParam String competency, @RequestParam String parentCompetency, @RequestParam String companyId,
           HttpSession session, @RequestParam String token) throws Exception{  
		
		List<String> questions =   questionService.fetchTextBasedQuestions(prompt, competency, parentCompetency);
		List<Question> list = new ArrayList<>();
			for(String q : questions) {
				Question qs = Question.builder().build();
				qs.setCompanyId(companyId);
				qs.setCompetency(competency);
				qs.setParentCompetency(parentCompetency);
				qs.setQuestionType(Question_Type.SUBJECTIVE.getType());
				qs.setPublished(false);
				qs.setQuestionText(q);
				questionService.saveOrUpdate(qs);
				list.add(qs);
			}
			
		return ResponseEntity.ok(list);
	}
	
	 @RequestMapping(value="uploadQuestions",method=RequestMethod.POST)  
	 @CrossOrigin
	    public ResponseEntity<?> uploadQuestions( @RequestParam MultipartFile file,  @RequestParam String companyId,
	           HttpSession session, @RequestParam String token) throws Exception{  
		 
		 try {
			 
			 if(!tenantService.isCompanyIdExisting(companyId)) {
				 return ResponseEntity.badRequest().body("Invalid Company Id "+companyId);
			 }
			 
			List<Question> questions = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, Question.class);
				System.out.println("Printing List Data: " +questions);
				for(Question q : questions) {
					
					if(!companyId.equals(q.getCompanyId())) {
						throw new RuntimeException("Non Existent Company Id "+q.getCompanyId());
					}
					
					
					
					String comp = q.getCompetency().trim();
					String parentComp = q.getParentCompetency().trim();
					if(q.getMultipleCompetenciesAssociatedWithQuestion() != null && q.getMultipleCompetenciesAssociatedWithQuestion().trim().length()>0) {
						String mult = q.getMultipleCompetenciesAssociatedWithQuestion();
						String diffParents[] = mult.split("###");
						for(String diffParent : diffParents) {
							System.out.println("diffParent "+diffParent);
						
							String comb[] = StringUtils.split(diffParent, "$$$");
							System.out.println("comb 0"+comb[0]);
							System.out.println("comb 0"+comb[1]);
							String parent = comb[0];
							String childs[] = comb[1].split("__");
							for(String c : childs) {
								c = c.trim();
								Competency competency =  competencyService.findByCompetencyAndParentCompetency(c, parent, companyId);
								if(competency == null) {
									 return ResponseEntity.badRequest().body("Competency doesnt exist "+c+". parent - "+parent);
								}
							}
							
						}
						
					}
					else {
						Competency competency =  competencyService.findByCompetencyAndParentCompetency(comp, parentComp, companyId);
						
						if(competency == null) {
							 return ResponseEntity.badRequest().body("Competency doesnt exist "+comp+". parent - "+parentComp);
						}
					}
					
				
					
					if(Question_Type.valueOf(q.getQuestionType()) == null) {
						 return ResponseEntity.badRequest().body("Invalid Question Type for  "+comp+". parent - "+parentComp+". question "+q.getQuestionText());
					}
					
					if(q.getQuestionType().equalsIgnoreCase(Question_Type.MCQ.getType())) {
						String rt[] = q.getRightChoice().split(",");
						List<String> list = Arrays.asList("Choice 1", "Choice 2", "Choice 3", "Choice 4", "Choice 5", "Choice 6");
						for(String r : rt) {
							r = r.trim();
							if(!list.contains(r)) {
								return ResponseEntity.badRequest().body("Invalid Right Choices - "+r+" "+comp+". parent - "+parentComp+". question "+q.getQuestionText());
							}
						}
					}
					q.setQuestionNonAI(true);
					q.setPublished(true);
					
					//q = fetchAudioForQ(q);
					questionService.saveOrUpdate(q);
					
				}
				
				 return ResponseEntity.ok("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error "+e.getMessage());
		}
	 }
	 
	 private Question fetchAudioForQ(Question q) {
		 if(q.getAnalyzeSound()) {
			Response res =  ResembleUtil.fetchAudio(q.getQuestionText());
			q.setAudioLinkExternal(res.getItem().getAudioSrc());
			String internalUrl = downloadAndShareInternalUrl(res.getItem().getAudioSrc(), q);
			q.setAudioLinkInternal(internalUrl);
		 }
		 
		return q; 
	 }
	 
	 private  String downloadAndShareInternalUrl(String url, Question q)  {
	        try (InputStream in = new URL(url).openStream()) {
	        	String baseLoc = config.getFileServerPath();
				String loc = "";
				Integer qCode = q.getQuestionText().hashCode() ;
					if(qCode < 0) {
						qCode = qCode * (-1);
					}
				 loc = baseLoc + java.io.File.separator + q.getCompanyId() +java.io.File.separator + q.getParentCompetency() +File.separator+q.getCompetency()+File.separator+qCode;
				 File folder = new File(loc);
				 folder.mkdirs();
				 File file = new File(loc+File.separator+"result.wav");
	            Files.copy(in, Paths.get(file.getAbsolutePath()));
	            String fileUrl = config.getFileServerBaseUrl()+"/"+q.getCompanyId()+"/"+q.getParentCompetency()+"/"+q.getCompetency()+"/"+qCode+"/result.wav";
				System.out.println(fileUrl);
				return fileUrl;
	        }
	        catch(Exception e) {
	        	throw new RuntimeException(e);
	        }
	    }
	
}
