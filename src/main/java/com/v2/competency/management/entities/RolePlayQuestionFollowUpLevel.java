package com.v2.competency.management.entities;

public enum RolePlayQuestionFollowUpLevel {
	
START("START"), FOLLOW_UP_1("FOLLOW_UP_1"), FOLLOW_UP_2("FOLLOW_UP_2"), FOLLOW_UP_3("FOLLOW_UP_3"), FOLLOW_UP_4("FOLLOW_UP_4"), FOLLOW_UP_5("FOLLOW_UP_5");
	
	String level;
	
	private RolePlayQuestionFollowUpLevel(String level) {
		this.level = level;
	}

	public String getlevel() {
		return level;
	}

}
