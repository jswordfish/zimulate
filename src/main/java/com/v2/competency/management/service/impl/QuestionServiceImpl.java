package com.v2.competency.management.service.impl;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.v2.competency.management.dtos.QuestionAvailabilityCountDto;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.repos.QuestionRepo;
import com.v2.competency.management.service.QuestionService;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {
	
	@Autowired
	QuestionRepo questionRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	String projectId = "contactaiassessments";
    String location = "asia-south1";
    String modelName = "gemini-1.0-pro-vision";
    
    ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Question findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(String questionText,
			String competency, String parentCompetency, String companyId) {
		return questionRepo.findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(questionText, competency, parentCompetency, companyId);
	}

	@Override
	public Page<Question> findAllQuestionsByCompetencyForCompany(String competency, String parentCompetency,
			String companyId, Pageable pageable) {
		return questionRepo.findAllQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, pageable);
	}

	@Override
	public Page<Question> findPublishedQuestionsByCompetencyForCompany(String competency, String parentCompetency,
			String companyId, Pageable pageable) {
		return questionRepo.findPublishedQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, pageable);
	}

	@Override
	public Page<Question> findUnPublishedQuestionsByCompetencyForCompany(String competency, String parentCompetency,
			String companyId, Pageable pageable) {
		return questionRepo.findUnPublishedQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, pageable);
	}

	@Override
	public Question saveOrUpdate(Question question) {
		Question question2 = findByQuestionTextAndCompetencyAndParentCompetencyAndCompanyId(question.getQuestionText(), question.getCompetency(), question.getParentCompetency(), question.getCompanyId());
			if(question2 == null) {
				question.setCreateDate(new Date());
				return questionRepo.save(question);
			}
		question.setId(question2.getId());	
		question.setCreateDate(question2.getCreateDate());
		question.setUpdateDate(new Date());
		question.setId(question2.getId());
		mapper.map(question, question2);
		return questionRepo.save(question2);
		
	}

	@Override
	public void deleteQuestion(Long id) {
		// TODO Auto-generated method stub
		//questionRepo.deleteById(id);
		Question q = questionRepo.findById(id).get();
		q.setSoftDelete(true);
		q.setUpdateDate(new Date());
		questionRepo.save(q);
	}

	@Override
	public void publishQuestion(Long id) {
		Question q = questionRepo.findById(id).get();
		if(q == null) {
			throw new RuntimeException("Question with id "+id+" does not exist");
		}
		
		q.setPublished(true);
		questionRepo.save(q);
	}

	@Override
	public void unPublishQuestion(Long id) {
		Question q = questionRepo.findById(id).get();
		if(q == null) {
			throw new RuntimeException("Question with id "+id+" does not exist");
		}
		
		q.setPublished(false);
		questionRepo.save(q);

	}

	@Override
	public List<Question> fetchMCQQuestions(String prompt, String competency, String parentCompetency) {
		prompt = URLDecoder.decode(prompt);
		prompt += " Response should not include any label or header and should only be a json array. Questions should not start with a serial number or a question sequence.";
		System.out.println("prompt is "+prompt);
		try (VertexAI vertexAI = new VertexAI(projectId, location)) {
		       GenerativeModel model = new GenerativeModel(modelName, vertexAI);
		      
		       GenerateContentResponse response = model.generateContent(
			    		  prompt
			      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		 //   res = res.replaceAll("\\p{Punct}", "");
		    res = res.replace("`", "");
		    res = res.replace('\u00A0',' ');
			  res = res.replace("'", " ");
		   System.out.println("res is "+res);
		   
		   System.out.println("-----------------------------------------------------");
		   CollectionType typeReference =
				    TypeFactory.defaultInstance().constructCollectionType(List.class, Question.class);
		  objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		  objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		   List<Question> questions = objectMapper.readValue(res, typeReference);
		    
		    return questions;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
	    	}
		
	}
	
	@Override
	public List<String> fetchTextBasedQuestions(String prompt, String competency, String parentCompetency) {
		
		prompt = URLDecoder.decode(prompt);
		prompt += " Response should not include any label or header and should only be a json array. Questions should not start with a serial number or a question sequence.";
		try (VertexAI vertexAI = new VertexAI(projectId, location)) {
		       GenerativeModel model = new GenerativeModel(modelName, vertexAI);
		      
		       GenerateContentResponse response = model.generateContent(
			    		  prompt
			      );
		   
		    String res =  response.getCandidates(0).getContent().getParts(0).getText();
		
		  
		   CollectionType typeReference =
				    TypeFactory.defaultInstance().constructCollectionType(List.class, String.class);
		  res = res.replace('\u00A0',' ');
		  res = res.replace("'", " ");
		  res = res.replace("`", "");
		  System.out.println("prompt is "+prompt);
		  System.out.println(" response is "+System.lineSeparator());
		  System.out.println(res);
		  System.out.println(" end response ");
		   ObjectMapper objectMapper1 = new ObjectMapper();
		   objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		   objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		//   objectMapper1.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		   List<String> questions = objectMapper1.readValue(res, typeReference);
		    
		    return questions;
		    }
		    catch(Exception e) {
		    	throw new RuntimeException(e);
	    	}
		
	}

	@Override
	public Page<Question> findPublishedMCQQuestionsByCompetencyForCompany(String competency, String parentCompetency,
			String companyId, Pageable pageable) {
		return questionRepo.findPublishedMCQQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, pageable);
	}

	@Override
	public Page<Question> findPublishedTextBasedQuestionsByCompetencyForCompany(String competency,
			String parentCompetency, String companyId, Pageable pageable) {
		return questionRepo.findPublishedTextBasedQuestionsByCompetencyForCompany(competency, parentCompetency, companyId, pageable);
	}

	@Override
	public Question findById(Long id) {
		// TODO Auto-generated method stub
		return questionRepo.findById(id).get();
	}

	@Override
	public List<Question> findRandomQuestionsByCompetencyForCompanyWithType(String competency, String parentCompetency,
			String questionType, String companyId, Integer numOfQs) {
		// TODO Auto-generated method stub
		return questionRepo.findRandomQuestionsByCompetencyForCompanyWithType(competency, parentCompetency, questionType, companyId, numOfQs);
	}

	@Override
	public Page<Question> findPublishedQuestionsByCompetencyAndQuestionTypeForCompany(String questionType,
			String competency, String parentCompetency, String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return questionRepo.findPublishedQuestionsByCompetencyAndQuestionTypeForCompany(questionType, competency, parentCompetency, companyId, pageable);
	}

	@Override
	public List<QuestionAvailabilityCountDto> findQuestionAvailabilityCount(String competency, String parentCompetency,
			String companyId) {
		// TODO Auto-generated method stub
		return questionRepo.findQuestionAvailabilityCount(competency, parentCompetency,"%"+competency+"%", "%"+parentCompetency+"%", companyId);
	}

	@Override
	public List<Question> findAllQuestionsAssociatedWithMultipleCompetenciesByCompetencyForCompany(String questionType, String competency,
			String parentCompetency, String companyId) {
		// TODO Auto-generated method stub
		return questionRepo.findAllQuestionsAssociatedWithMultipleCompetenciesByCompetencyForCompany(questionType, "%"+competency+"%", "%"+parentCompetency+"%", companyId);
	}

}
