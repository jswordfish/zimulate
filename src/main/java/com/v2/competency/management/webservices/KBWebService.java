package com.v2.competency.management.webservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.v2.competency.management.dtos.CompetencyDto;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.VFQuestionResponseDto;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Source;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.service.AssessmentMapperService;
import com.v2.competency.management.service.QuestionService;
import com.v2.competency.management.service.VFTestService;
@RestController
@CrossOrigin
public class KBWebService {
	/**
	 * VFTestService caters to both VF and KB tests
	 */
	@Autowired
	VFTestService testService;
	
	@Autowired
	QuestionService questionService;
	
	
	
	XmlMapper xmlMapper = new XmlMapper();

	@RequestMapping(value = "/initKbTest", method = RequestMethod.GET)
	public ResponseEntity<?> initKbTest(@RequestParam String testIdentifier,  @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
		if(test == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test identifier - "+testIdentifier+" or Comapny Id "+companyId);
		}
		
		if(test.getQuestionSource() == null || test.getQuestionSource().equals(Question_Source.AI.getSource())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid test source "+test.getQuestionSource());
		}
			
		String xml = test.getTestXmlForKB();
		CompetencyTest competencyTest = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
		if(competencyTest == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Competency Test xml can not be processed");
		}
		
		if(test.getQuestionSource().equals(Question_Source.QUESTION_BANK_FIXED.getSource())) {
			List<CompetencyDto> competencyDtos =  competencyTest.getKbCompetencies();
				for(CompetencyDto c : competencyDtos) {
					List<Long> ids =   c.getQuestionIds();
					List<Question> questions = new ArrayList<>();
					for(Long id : ids) {
						Question q = questionService.findById(id);
						questions.add(q);
					}
					c.setQuestions(questions);
				}
				test.setCompetencyTest(competencyTest);
				return ResponseEntity.ok(test);
		}
		else if(test.getQuestionSource().equals(Question_Source.QUESTION_BANK_RANDOM.getSource())){
			List<CompetencyDto> competencyDtos =  competencyTest.getKbCompetencies();
			for(CompetencyDto c : competencyDtos) {
				Integer noOfQs = c.getNoOfQuestionsToBeAsked();
				String questionType = c.getQuestionType();
				
				if(noOfQs == null || questionType == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both No of Questions and Question Type need to be configured for "+c.getCompetency()+" - "+c.getParentCompetency());
				}
				
				List<Question> questions = questionService.findRandomQuestionsByCompetencyForCompanyWithType(c.getCompetency(), c.getParentCompetency(), c.getQuestionType(), companyId, noOfQs);
				c.setQuestions(questions);
				List<Long> ids = new ArrayList<>();
				for(Question q : questions) {
					ids.add(q.getId());
				}
				c.setQuestionIds(ids);
			}
			test.setCompetencyTest(competencyTest);
			return ResponseEntity.ok(test);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test is not correctly configured");
	}
	
	
	
	@RequestMapping(value = "/nextQuestionKB", method = RequestMethod.GET)
	public ResponseEntity<?> nextQuestionKB(@RequestParam Long qid, @RequestParam String email, @RequestParam String testIdentifier,  @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
			if(test == null) {
				return ResponseEntity.ok(VFQuestionResponseDto.builder().error("error - test not exists").build());
			}
		String xml = test.getTestXmlForKB();
		CompetencyTest competencyTest = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
		if(competencyTest == null) {
			return ResponseEntity.ok(VFQuestionResponseDto.builder().error("error").build());
		}
		
		Question q = questionService.findById(qid);
		return ResponseEntity.ok(q);
		
	}

}
