package com.v2.competency.management.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemPromptGenerationResponse {
	
	private String updatedSystemPrompt;
	
	private String oppositionAgentPrompt;

}
