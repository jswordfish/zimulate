package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum IndiaFirstSalesPersona {
	
	The_Chameleon("The Chameleon", PersonaDescription.The_Chameleon, "MXGyTMlsvQgQ4BL0emIa"), 
	The_Logic_Ladder("The Logic Ladder", PersonaDescription.The_Logic_Ladder, "A7AUsa1uITCDpK29MG3m"),
	The_Provocative_Advisor("The Provocative Advisor", PersonaDescription.The_Provocative_Advisor, "TNHbwIMY5QmLqZdvjhNn"),
	The_Relationship_Anchor("The Relationship Anchor", PersonaDescription.The_Relationship_Anchor, "MXGyTMlsvQgQ4BL0emIa");
	
	String persona;
	
	String desc;
	
	String elevenLabsVoiceId;
	
	private IndiaFirstSalesPersona(String persona, String desc, String elevenLabsVoiceId) {
		this.persona = persona;
		this.desc = desc;
		this.elevenLabsVoiceId = elevenLabsVoiceId;
	}

	public String getPersona() {
		return persona;
	}

	public String getElevenLabsVoiceId() {
		return elevenLabsVoiceId;
	}
	
	
	public String getDesc() {
		return desc;
	}

	public List<TrainerPersonaDto> getAllDtos(){
		return Arrays.stream(IndiaFirstSalesPersona.values())
				.map(val -> TrainerPersonaDto.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
	}

}
