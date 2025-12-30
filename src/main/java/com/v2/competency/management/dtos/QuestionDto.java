package com.v2.competency.management.dtos;

import com.poiji.annotation.ExcelCellName;
import com.v2.competency.management.entities.Question_Type;

public class QuestionDto {
	
	Long qid;
	
String questionText;
	

	String competency;
	
	String parentCompetency;
	
	
	String questionType = Question_Type.MCQ.getType();
	
	String imageUrl;
	
	String choice1;
	
	String choice2;
	
	String choice3;
	
	String choice4;
	
	String choice5;
	
	String choice6;
	
	
	String rightChoice;
	
	
	Boolean published = false;
	
	
	String multipleCompetenciesAssociatedWithQuestion;
	
	/**
	 * If multipleCompetenciesAssociatedWithQuestion is not null, then we may need a single skill/generic competency label to represent all the multiple competencies.
	 */
	String singleLabelForMultipleCompetenciesAssociatedWithQuestion;


	public String getQuestionText() {
		return questionText;
	}


	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}


	public String getCompetency() {
		return competency;
	}


	public void setCompetency(String competency) {
		this.competency = competency;
	}


	public String getParentCompetency() {
		return parentCompetency;
	}


	public void setParentCompetency(String parentCompetency) {
		this.parentCompetency = parentCompetency;
	}


	public String getQuestionType() {
		return questionType;
	}


	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getChoice1() {
		return choice1;
	}


	public void setChoice1(String choice1) {
		this.choice1 = choice1;
	}


	public String getChoice2() {
		return choice2;
	}


	public void setChoice2(String choice2) {
		this.choice2 = choice2;
	}


	public String getChoice3() {
		return choice3;
	}


	public void setChoice3(String choice3) {
		this.choice3 = choice3;
	}


	public String getChoice4() {
		return choice4;
	}


	public void setChoice4(String choice4) {
		this.choice4 = choice4;
	}


	public String getRightChoice() {
		return rightChoice;
	}


	public void setRightChoice(String rightChoice) {
		this.rightChoice = rightChoice;
	}


	public Boolean getPublished() {
		return published;
	}


	public void setPublished(Boolean published) {
		this.published = published;
	}


	public String getChoice5() {
		return choice5;
	}


	public void setChoice5(String choice5) {
		this.choice5 = choice5;
	}


	public String getChoice6() {
		return choice6;
	}


	public void setChoice6(String choice6) {
		this.choice6 = choice6;
	}


	public Long getQid() {
		return qid;
	}


	public void setQid(Long qid) {
		this.qid = qid;
	}
	
	


}
