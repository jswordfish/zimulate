package com.v2.competency.management.webservices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.v2.competency.management.dtos.CompetencyQuestion;
import com.v2.competency.management.dtos.CompetencyTest;
import com.v2.competency.management.dtos.VFQuestionResponseDto;
import com.v2.competency.management.entities.Tenant;
import com.v2.competency.management.entities.VFTest;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.service.VFTestService;
import com.v2.competency.management.service.VFTestUserQuestionAnswerService;
import com.v2.competency.management.service.VFTestUserSessionService;

@RestController
@CrossOrigin
public class VFWebService {
	
	@Autowired
	VFTestService testService;
	
	@Autowired
	VFTestUserQuestionAnswerService answerService;
	
	XmlMapper xmlMapper = new XmlMapper();
	
	@Autowired
	VFTestUserSessionService sessionService;
	static final String BASIC_GOVERNNANCE = System.lineSeparator()+"The question to be fetched SHOULD NOT be amongst those listed below OR should be different from those listed below (between square brackets). \n"
			+ "[\n"
			+ "${QUESTIONS}\n"
			+ "]";
	
	
	private String computeInstructionsBeforeQ( String email, String testIdentifier, String lastQuestionAsked, @RequestParam String companyId) {
		Integer att = sessionService.findCountOfSessionsForUserFotTest(email, companyId, testIdentifier) + 1;
		 List<String> priorQuestions =  answerService.findAllQuesTextForUser(testIdentifier, email, companyId, att);
		 String inst = "";
		 int count = 1;
		 for(String q : priorQuestions) {
			 inst += ""+count+". "+q+System.lineSeparator();
			 count++;
		 }
		 	if(priorQuestions.size() == 0) {
		 		return "";
		 	}
		 String ret = BASIC_GOVERNNANCE;
		 ret = ret.replace("${QUESTIONS}", inst);
		 System.out.println("$$$ "+System.lineSeparator()+""+ret+""+System.lineSeparator());
		return ret;
	}
	
	
	
	@RequestMapping(value = "/nextQuestion", method = RequestMethod.GET)
	public ResponseEntity<?> getNextQuestion(@RequestParam String email, @RequestParam String testIdentifier, @RequestParam String lastQuestionAsked, @RequestParam String companyId, @RequestParam String token)
			throws Exception {
		
		VFTest test = testService.findByTestIdentifier(testIdentifier, companyId);
			if(test == null) {
				return ResponseEntity.ok(VFQuestionResponseDto.builder().error("error - test not exists").build());
			}
		String xml = test.getTestXml();
		CompetencyTest competencyTest = xmlMapper.readValue(xml.getBytes(), CompetencyTest.class);
		if(competencyTest == null) {
			return ResponseEntity.ok(VFQuestionResponseDto.builder().error("error").build());
		}
		else {
		Map<String, CompetencyQuestion> map = process(competencyTest);
			if(!lastQuestionAsked.equalsIgnoreCase("na")) {
				try {
					Integer.parseInt(lastQuestionAsked);
				}
				catch(Exception e) {
					return ResponseEntity.ok(VFQuestionResponseDto.builder().error("error - lastQuestionAsked should be na or a number").build());
				}
			}
		
		
			if(lastQuestionAsked.equalsIgnoreCase("na")) {
				CompetencyQuestion q = map.get("1");
				q.setFollowup(false);
				q.setFollowups(null);
				q.setTimeWhenQAsked(Calendar.getInstance().getTimeInMillis());
				return ResponseEntity.ok(VFQuestionResponseDto.builder().question(q).build());
			}
			else {
				Integer seq = Integer.parseInt(lastQuestionAsked);
				String expected = seq+""+1;
				if(map.get(expected) != null) {
					CompetencyQuestion q = map.get(expected);
					q.setFollowups(null);
						if(Integer.parseInt(q.getQid()) > 10) {
							q.setFollowup(true);
						}
						else {
							q.setFollowup(false);
						}
						q.setTimeWhenQAsked(Calendar.getInstance().getTimeInMillis());
						q.setInstructions(computeInstructionsBeforeQ(email, testIdentifier, lastQuestionAsked, companyId));
					return ResponseEntity.ok(VFQuestionResponseDto.builder().question(q).build());
				}
				else {
					expected = ""+(seq+1);
					CompetencyQuestion next = get(map, expected);

						if(next == null) {
							CompetencyQuestion ret = CompetencyQuestion.builder().status("done").build();
							VFQuestionResponseDto dto =  VFQuestionResponseDto.builder().question(ret).build();
							//return ResponseEntity.ok(VFQuestionResponseDto.builder().status("done").build());
							return ResponseEntity.ok(dto);
						}
						if(Integer.parseInt(next.getQid()) > 10) {
							next.setFollowup(true);
						}
						else {
							next.setFollowup(false);
						}
					next.setFollowups(null);
					next.setTimeWhenQAsked(Calendar.getInstance().getTimeInMillis());
					next.setInstructions(computeInstructionsBeforeQ(email, testIdentifier, lastQuestionAsked, companyId));
					return ResponseEntity.ok(VFQuestionResponseDto.builder().question(next).build());
				}
			}
		}
	}
	
	private CompetencyQuestion get(Map<String, CompetencyQuestion> map, String id) {
		//id passed is not 1 digit to begin with
		Integer seq = Integer.parseInt(id);
		if(map.get(id) != null) {
			return map.get(id);
		}
		else {
			if(id.length() == 1) {
				return null; // no further q
			}
			String temp = id.substring(0, id.length() - 1);
			seq = Integer.parseInt(temp) + 1;
			return get(map, ""+seq);
			
		}
	}
	
	private Map<String, CompetencyQuestion> process(CompetencyTest competencyTest){
		Map<String, CompetencyQuestion> map = new HashMap<>();
		for(CompetencyQuestion q : competencyTest.getCompetencies()) {
			map.put(q.getQid(), q);
				if(q.getFollowups() == null) {
					q.setFollowups(new ArrayList<>());
				}
				for(CompetencyQuestion q_f : q.getFollowups()) {
					map.put(q_f.getQid(), q_f);
						if(q_f.getFollowups() == null) {
							q_f.setFollowups(new ArrayList<>());
						}
						for(CompetencyQuestion q_f_f : q_f.getFollowups()) {
							map.put(q_f_f.getQid(), q_f_f);
						}
				}
		}
		
		return map;
	}
	
	/*
	 * @RequestMapping(value="createVFTest",method=RequestMethod.POST) public
	 * ResponseEntity<?> createVFTest( @RequestBody VFTest test, HttpSession
	 * session, @RequestParam String token) throws Exception{
	 * 
	 * if(test.getCompetencyTest() == null) { return
	 * ResponseEntity.status(HttpStatus.BAD_REQUEST).
	 * body("Competency Test json not passed"); } String xml =
	 * xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test.
	 * getCompetencyTest()); test.setTestXml(xml); testService.saveOrUpdate(test);
	 * return ResponseEntity.ok("ok"); }
	 */
	
	
	@RequestMapping(value = "/allAnswers", method = RequestMethod.GET)
	public ResponseEntity<?> allAnswers(@RequestParam String testIdentifier, @RequestParam String email, @RequestParam String companyId, @RequestParam Integer attempt, @RequestParam String token)
			throws Exception {
		List<VFTestUserQuestionAnswer> answers =  answerService.findAllQAForUser( testIdentifier, email, companyId, attempt);
		
		return ResponseEntity.ok(answers);
	}
	

}
