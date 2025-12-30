package com.v2.competency.management.dtos;

import java.util.List;

import javax.persistence.Entity;

import com.v2.competency.management.entities.Competency;

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
public class CompetencyTest {
	
	List<CompetencyQuestion> competencies;
	
	
	List<CompetencyDto> kbCompetencies;

}
