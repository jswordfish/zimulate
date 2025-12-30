package com.v2.competency.management.dtos;

import javax.persistence.Entity;

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
public class VFQuestionResponseDto {
	
	CompetencyQuestion question;
	
	String error;
	
	String status;

}
