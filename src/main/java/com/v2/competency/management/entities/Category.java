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
public class Category extends Base{
	
	@ExcelCellName(value = "Category")
	String category;
	
	@ExcelCellName(value = "Sub Category")
	String subCategory;
	
	@ExcelCellName(value = "Competency")
	String competency;
	
	@ExcelCellName(value = "Parent Competency")
	String parentCompetency;
	
	@ExcelCellName(value = "Test Name")
	String assessmentName;
	
	String publicTestLink;

}
