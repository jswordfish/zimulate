package com.v2.competency.management.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RolePlayTestDto {
	
	@ExcelCellName(value = "Test Name")
	String testName;
	
	
	@ExcelCellName(value = "Duration")
	Integer duration;
	
	@ExcelCellName(value = "Competency")
	String competency;
	
	@ExcelCellName(value = "Parent Competency")
	String parentCompetency;
	
	@ExcelCellName(value = "Question Text")
	String questionText;
	
	@ExcelCellName(value = "Product Info")
	String productInfo;
	
	@ExcelCellName(value = "Competition Info")
	String competitionInfo;
	
	
	@ExcelCellName(value = "Started By User")
	Boolean isTestStartByUser = false;
	
	@ExcelCellName(value = "Evaluation Params")
	String commaSeparatedAnalysisParams;
	
	
	@ExcelCellName(value = "Roleplay Objective")
	String rolePlayObjective;
	
	@ExcelCellName(value = "Analysis Prompt Easy")
	String analysisGenPromptEasy;
	
	@ExcelCellName(value = "Analysis Prompt Medium")
	String analysisGenPromptMedium;
	
	@ExcelCellName(value = "Analysis Prompt Difficult")
	String analysisGenPromptHard;
	
	@ExcelCellName(value = "Company ID")
	String companyId;
	
	@ExcelCellName(value = "Industry")
	String industry;
	
	@ExcelCellName(value = "Published")
	Boolean published;
	
	@ExcelCellName(value = "Question Prompt")
	String defaultQuestionPrompt;
	
	@ExcelCellName(value = "Roleplay UI Label")
	String rolePlayLabelForUI;
	
	@ExcelCellName(value = "Agent Id")
	String agentId;
	
	@ExcelCellName(value = "Report Version")
	String reportVersion;
	
	@ExcelCellName(value = "User Persona")
	String userPersona;
	
	@ExcelCellName(value = "AI Persona")
	String aiPersona;
	
	@ExcelCellName(value = "Roleplay Type")
	String rolePlayType;
	
	Boolean notShowCustomPersona;
	
	Boolean isTranscriptBasedAnalysis;
	
}
