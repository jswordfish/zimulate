package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.CompetencyAssessmentMapping;

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
public class ConsolidatedAssessmentDto {
	
	String groupName;
	
	List<CompetencyAssessmentMapping> list;

}
