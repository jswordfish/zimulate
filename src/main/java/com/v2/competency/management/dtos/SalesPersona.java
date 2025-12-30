package com.v2.competency.management.dtos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SalesPersona {
	
	Bargain_Hunter("Bargain Hunter", SalesPersonaDescription.BARGAIN_HUNTER, "TNHbwIMY5QmLqZdvjhNn"), Skeptical_Analyst("Skeptical Analyst", SalesPersonaDescription.SKEPTICAL_ANALYST, "TNHbwIMY5QmLqZdvjhNn"), Time_Strapped_Executive("Time-Strapped Executive", SalesPersonaDescription.TIME_STRAPPED_EXECUTIVE, "mbL34QDB5FptPamlgvX5"), Indecisive_Procrastinator("Indecisive Procrastinator", SalesPersonaDescription.INDECISIVE_PROCRASTINATOR, "vO7hjeAjmsdlGgUdvPpe"),
	Know_It_All("Know-It-All", SalesPersonaDescription.KNOW_IT_ALL, "DMyrgzQFny3JI1Y1paM5"),
	Loyal_Incumbent("Loyal Incumbent", SalesPersonaDescription.LOYAL_INCUMBENT, "8xsdoepm9GrzPPzYsiLP"),
	Budget_Constrained_SMB_Owner("Budget-Constrained SMB Owner", SalesPersonaDescription.BUDGET_CONSTRAINED_SMB_OWNER, "u7bRcYbD7visSINTyAT8"),
	RELATIONSHIP_BUILDER("Relationship Builder Hunter", SalesPersonaDescription.RELATIONSHIP_BUILDER, "7QwDAfHpHjPD14XYTSiq");
	
	String persona;
	
	String desc;
	
	String elevenLabsVoiceId;
	
	private SalesPersona(String persona, String desc, String elevenLabsVoiceId) {
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

	public List<SalesPersonaDTO> getAllDtos(){
		return Arrays.stream(SalesPersona.values())
				.map(val -> SalesPersonaDTO.builder().persona(val.getPersona()).personaDesc(val.getDesc()).voiceId(val.getElevenLabsVoiceId()).build())
				.collect(Collectors.toList());
	}

}
