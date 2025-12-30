package com.v2.competency.management.dtos;

import java.util.List;

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
public class WorkflowAssignmentDto {
	
	List<String> emails;
	
	List<Long> workflowIds;
	
	String companyId;

}
