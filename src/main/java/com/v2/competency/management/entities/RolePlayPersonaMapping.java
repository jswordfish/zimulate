package com.v2.competency.management.entities;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RolePlayPersonaMapping extends Base{
	
	String persona;
	
	String personaDescription;
	
	String voiceId;
	
	String type;

}
