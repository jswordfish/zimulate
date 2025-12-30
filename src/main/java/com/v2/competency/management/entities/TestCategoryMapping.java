package com.v2.competency.management.entities;

import javax.persistence.Entity;

import com.poiji.annotation.ExcelCellName;

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
public class TestCategoryMapping extends Base{
	@ExcelCellName(value = "Category")
	String category;
	
	
	@ExcelCellName(value = "Sub Category")
	String subCategory;
	
	@ExcelCellName(value = "Category of Sub Category")
	String subSubCategory;
	
	@ExcelCellName(value = "Objectives")
	String objectives;
	
	@ExcelCellName(value = "Evaluation Parameters")
	String parametersOfEvaluation;
	
	@ExcelCellName(value = "Test Name")
	String testName;
	
	@ExcelCellName(value = "Test Identifier")
	String testIdentifier;

}
