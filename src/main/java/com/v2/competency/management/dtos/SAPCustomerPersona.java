package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SAPCustomerPersona {
	
	CFO("CFO (Chief Financial Officer)", PersonaDescription.CFO, "G17SuINrv2H9FC6nvetn"), 
	CPO("CPO (Chief Procurement Officer)", PersonaDescription.CPO, "goT3UYdM9bhm0n2lmKQx"),
	COO("COO (Chief Operating Officer)", PersonaDescription.COO, "qxTFXDYbGcR8GaHSjczg"),
	CHRO("CHRO (Chief Human Resources Officer)", PersonaDescription.CHRO, "mZ8K1MPRiT5wDQaasg3i"),
	CRO("CRO (Chief Revenue Officer)", PersonaDescription.CRO, "Fahco4VZzobUeiPqni1S"),
	CIO("CIO (Chief Information Officer)", PersonaDescription.CIO, "yhf80q1381zd2JJQ4tM7");
	
	
	String persona;
	
	String desc;
	
	String elevenLabsVoiceId;
	
	private SAPCustomerPersona(String persona, String desc, String elevenLabsVoiceId) {
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
		return Arrays.stream(SAPCustomerPersona.values())
				.map(val -> TrainerPersonaDto.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
	}

}
