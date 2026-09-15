package com.v2.competency.management.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

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
public class VFRolePlayTest extends Base{
	
	//@ExcelCellName(value = "Test Name")
	String testName;
	
	
	//@ExcelCellName(value = "Duration")
	Integer duration;
	
	//@ExcelCellName(value = "Question Text")
	@Lob
	String questionText;
	
	@Lob
	String productInfo;
	
	@Lob
	String competitionInfo;
	
	String publicTestLink;
	
	Boolean isTestStartByUser = false;
	
	String competency;
	
	String parentCompetency;
	
	
	
	//@ExcelCellName(value = "Evaluation Params")
	@Column(length = 2500)
	String commaSeparatedAnalysisParams;
	
	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "roleplay_analysis_structure_id", referencedColumnName = "id")
	RoleplayAnalysisStructure roleplayAnalysisStructure;
	
	String industry;
	
	Boolean published;
	
	Boolean isTranscriptBasedAnalysis;
	
	@Column(name = "default_question_prompt", length = 2500)
	String defaultQuestionPrompt;
	
	String rolePlayLabelForUI;
	
	String agentId;
	
	String knowledgebaseId;
	
	String reportVersion;
	
	String userPersona;
	
	String aiPersona;
	
	String rolePlayType;
	
	Boolean notShowCustomPersona;
	
	
}
