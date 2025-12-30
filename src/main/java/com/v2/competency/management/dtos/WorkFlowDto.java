package com.v2.competency.management.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class WorkFlowDto {
	
	String name;
	
	String objective;
	
	String industry;
	
	Long id;
	
	String companyId;
	
	Boolean complete;

}
