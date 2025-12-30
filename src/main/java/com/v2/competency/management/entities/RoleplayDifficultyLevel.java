package com.v2.competency.management.entities;

public enum RoleplayDifficultyLevel {
	
	
	
	DEFAULT("DEFAULT"), EASY("EASY"), MEDIUM("MEDIUM"), HARD("HARD");
	
	String level;
	
	private RoleplayDifficultyLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}
	
	

}
