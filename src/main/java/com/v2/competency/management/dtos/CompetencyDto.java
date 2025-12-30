package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.Question_Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyDto {
	
	String competency;
	
	String parentCompetency;
	
	@Builder.Default
	Boolean questionsAskedInRandom = false;
	
	/**
	 * Need to be set for KB random test
	 */
	Integer noOfQuestionsToBeAsked;
	
	List<Long> questionIds;
	
	List<Question> questions;
	
	/**
	 * Need to be set for KB random test
	 */
	@Builder.Default
	String questionType = Question_Type.MCQ.getType();

}
