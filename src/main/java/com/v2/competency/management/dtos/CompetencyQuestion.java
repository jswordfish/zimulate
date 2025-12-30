package com.v2.competency.management.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

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
public class CompetencyQuestion implements Serializable{
	
	private static final long serialVersionUID = 4588691944457941921L;

	String qid;
	
	String competency;
	
	String parentCompetency;
	
	@Builder.Default
	String proficiencyLevel = Proficiency.LEVEL3.getLevel();
	
	@Builder.Default
	List<CompetencyQuestion> followups = new ArrayList<>();
	
	
	@Builder.Default
	String evaluationParameters = "Evaluate the user answer on following 4 parameters - Relevance to Question, Correctness, Depth of the Answer, Coverage to all areas of Question";
	
	@Builder.Default
	Integer weightOfQuestion = 0;
	
	Boolean followup;
	
	@Builder.Default
	String status = "";
	
	Long timeWhenQAsked;
	
	@Builder.Default
	String instructions = "";

}
