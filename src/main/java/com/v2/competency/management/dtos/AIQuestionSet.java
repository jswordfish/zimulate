package com.v2.competency.management.dtos;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

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
public class AIQuestionSet {
	
	@Builder.Default
	List<AIQuestion> questions = new ArrayList<>();
	
	

}
