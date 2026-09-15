package com.v2.competency.management.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Entity
public class RoleplayAnalysisStructure extends Base{
	
	String rolePlayObjective;
	
	@Column(length = 2500)
	String analysisGenPromptEasy;
	
	@Column(length = 2500)
	String analysisGenPromptMedium;
	
	@Column(length = 2500)
	String analysisGenPromptHard;
	
	String analysisGenPromptTranscript;
	
	@OneToOne(mappedBy = "roleplayAnalysisStructure")
	@JsonIgnore
	VFRolePlayTest  rolePlayTest;

}
