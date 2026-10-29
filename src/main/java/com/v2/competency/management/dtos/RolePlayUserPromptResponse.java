package com.v2.competency.management.dtos;

import com.v2.competency.management.entities.VFRolePlayTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePlayUserPromptResponse {

    private VFRolePlayTest test;
    
    private String userPrompt;
    
}
