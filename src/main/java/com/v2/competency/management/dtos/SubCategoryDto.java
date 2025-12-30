package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.VFTest;

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
public class SubCategoryDto {
	
	String category;
	String subCategory;
	
	List<VFTest> assessments;

}
