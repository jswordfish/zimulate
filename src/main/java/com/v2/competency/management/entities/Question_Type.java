package com.v2.competency.management.entities;

public enum Question_Type {
	
 MCQ("MCQ"), SUBJECTIVE("SUBJECTIVE"), SURVEY("SURVEY");
	
	String type;
	
	private Question_Type(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
