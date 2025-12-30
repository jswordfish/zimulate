package com.v2.competency.management.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question extends Base{
	
	@ExcelCellName(value = "Question Text")
	@Column(length = 3000)
	String questionText;
	
	@ExcelCellName(value = "Question Text With Html Tags")
	@Column(length = 3000)
	String questionTextLabel;
	
	@ExcelCellName(value = "Competency")
	@JsonIgnore
	String competency;
	
	@ExcelCellName(value = "Parent Competency")
	@JsonIgnore
	String parentCompetency;
	
	@JsonIgnore
	@Builder.Default
	@ExcelCellName(value = "Question Type")
	String questionType = Question_Type.MCQ.getType();
	
	String imageUrl;
	
	@ExcelCellName(value = "Choice 1")
	String choice1;
	
	@ExcelCellName(value = "Choice 2")
	String choice2;
	
	@ExcelCellName(value = "Choice 3")
	String choice3;
	
	@ExcelCellName(value = "Choice 4")
	String choice4;
	
	@ExcelCellName(value = "Choice 5")
	String choice5;
	
	@ExcelCellName(value = "Choice 6")
	String choice6;
	
	@ExcelCellName(value = "Right Choices")
	String rightChoice;
	
	@ExcelCellName(value = "Publish")
	@JsonIgnore
	@Builder.Default
	Boolean published = false;
	
	Boolean softDelete;
	
	@Builder.Default
	Boolean questionNonAI = false;
	
	
	/**
	 * applicable if a question is associated with multiple competencies & u want a custom prompt to generate insights.
	 */
	@ExcelCellName(value = "Optional AiInsights Prompt")
	@Column(length = 3000)
	String aiInsightsPrompt;
	
	/**
	 * Typically a Q is associated with a single parent competency - competency combination. There might be scenarios, where a question might be associated with multiple cpompetencies. 
	 * To be noted - these multiple competencies should pre-exist in the system
	 */
	@ExcelCellName(value = "Multiple Competencies Associated With Q")
	@Column(length = 1000)
	String multipleCompetenciesAssociatedWithQuestion;
	
	/**
	 * If multipleCompetenciesAssociatedWithQuestion is not null, then we may need a single skill/generic competency label to represent all the multiple competencies.
	 */
	@ExcelCellName(value = "Single Label For Multiple Competencies")
	String singleLabelForMultipleCompetenciesAssociatedWithQuestion;
	
	
	@Builder.Default
	@ExcelCellName(value = "Analyze Sound?")
	Boolean analyzeSound = false;
	
	String audioLinkExternal;
	
	String audioLinkInternal;
}
