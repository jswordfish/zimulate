package com.v2.competency.management.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePlayUserPromptRequest {

    private String testName;
    
    private String companyId;
    
    private String userPrompt;
    
}
