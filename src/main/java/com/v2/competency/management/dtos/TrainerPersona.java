package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TrainerPersona {
	
	The_Relatable_StoryTeller("The Relatable Storyteller", TrainerPersonaDescription.The_Relatable_StoryTeller, "BHyvQU4czkhWdOZH4Rdq"), 
	The_Precision_Architect("The Precision Architect", TrainerPersonaDescription.The_Precision_Architect, "A7AUsa1uITCDpK29MG3m");
	
	String persona;
	
	String desc;
	
	String elevenLabsVoiceId;
	
	private TrainerPersona(String persona, String desc, String elevenLabsVoiceId) {
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
		return Arrays.stream(TrainerPersona.values())
				.map(val -> TrainerPersonaDto.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
	}

}
