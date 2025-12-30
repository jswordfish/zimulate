package com.v2.competency.management.entities;

import javax.persistence.Entity;

import com.poiji.annotation.ExcelCellName;
import com.v2.competency.management.dtos.Proficiency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyAssessmentMapping extends Base{
	
	@ExcelCellName(value = "Competency")
	String competency;
	
	@ExcelCellName(value = "Parent Competency")
	String parentCompetency;
	
	@ExcelCellName(value = "Test Name")
	String testName;
	
	@ExcelCellName(value = "Test Id")
	Long testId;
	
	@ExcelCellName(value = "Proficiency")
	String proficiencyLevel = Proficiency.LEVEL3.getLevel();
	
	
	@ExcelCellName(value = "Instructions")
	String specificInstructions;
	
	
	Boolean consolidatedAssessments;
	
	@ExcelCellName(value = "Disable Record")
	Boolean disableConsolidatedAssessments;
	
	@ExcelCellName(value = "Consolidated Assessment Group Name")
	String consolidatedAssessmentGroupName;
	

}
