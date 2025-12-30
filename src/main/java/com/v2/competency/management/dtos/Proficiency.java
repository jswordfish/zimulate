package com.v2.competency.management.dtos;

public enum Proficiency {
	
	LEVEL1("LEVEL1"), LEVEL2("LEVEL2"), LEVEL3("LEVEL3"), LEVEL4("LEVEL4"), LEVEL5("LEVEL5"), LEVEL6("LEVEL6");
	
	String level;
	
	private Proficiency(String level) {
		this.level = level;;
	}
	public String getLevel() {
		return level;
	}
	
	
}
