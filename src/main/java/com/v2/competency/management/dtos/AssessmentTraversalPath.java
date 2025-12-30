package com.v2.competency.management.dtos;

import java.util.ArrayList;
import java.util.List;

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
public class AssessmentTraversalPath {
	
	String email;
	
	String companyId;
	
	List<Path> level1Paths = new ArrayList<>();

}
