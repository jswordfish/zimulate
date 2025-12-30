package com.v2.competency.management.dtos;

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
public class AIObjectiveQuestion extends AIQuestion{
	
	String question;
	
	String choice1;
	
	String choice2;
	
	String choice3;
	
	String choice4;
	
	
	String correctChoices;
	

}
