package com.v2.competency.management.entities;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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
public class VideoAgentSession extends Base{
	
	
	
	
	
	String email;
	
	
	@OneToOne
	VideoAgent videoAgent;
	
	Integer attempt;

}
