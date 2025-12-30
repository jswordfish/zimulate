package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.AssessmentMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AssessmentAssignmentTypeDto {
	/**
	 * Can be role/competecy/job description
	 */
	String typeOfAssignment;
	
	/**
	 * Can be role name/competecy name/job description name
	 */
	String valueOfAssignment;
	
	List<AssessmentMapper> list;
	
	Float averageScore;

}
