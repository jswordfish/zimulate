package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SCI_CustomerPersona {
	
	Tech_Savvy_Family_Protector("Tech-Savvy Family Protector", TrainerPersonaDescription.Tech_Savvy_Family_Protector, "mbL34QDB5FptPamlgvX5"), 
	Community_Focused_Investor ("Community-Focused Investor", TrainerPersonaDescription.Community_Focused_Investor, "A7AUsa1uITCDpK29MG3m"),
	Budget_Concious_Pragmatic_Planner ("Budget-Conscious Pragmatic Planner", TrainerPersonaDescription.Budget_Concious_Pragmatic_Planner, "7QwDAfHpHjPD14XYTSiq"),
	Convenience_Driven_Busy_Parent ("Convenience-Driven Busy Parent", TrainerPersonaDescription.Convenience_Driven_Busy_Parent, "u7bRcYbD7visSINTyAT8"),
	Security_First_Privacy_Defender ("Security-First Privacy Defender", TrainerPersonaDescription.Security_First_Privacy_Defender, "GoGUcAZovo4MFeLxJdZd");
	
	
	String persona;
	
	String desc;
	
	String elevenLabsVoiceId;
	
	private SCI_CustomerPersona(String persona, String desc, String elevenLabsVoiceId) {
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
		return Arrays.stream(SCI_CustomerPersona.values())
				.map(val -> TrainerPersonaDto.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
	}

}
