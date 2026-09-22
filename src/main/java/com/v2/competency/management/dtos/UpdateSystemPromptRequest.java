package com.v2.competency.management.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemPromptRequest {

    private String currentSystemPrompt;

    private String userFeedback;
    
    private String userPersona;
    
	private String rolePlayObjective;
	
	private String transcript;
	
	private String updateprompt;
	
	private String oppositionPromptGenPrompt;
    
}
