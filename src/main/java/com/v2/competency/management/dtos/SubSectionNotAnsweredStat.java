package com.v2.competency.management.dtos;

public class SubSectionNotAnsweredStat {
	
	String subSection;
	
	Long countUnAnswered;
	
	

	public SubSectionNotAnsweredStat() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SubSectionNotAnsweredStat(String subSection, Long countUnAnswered) {
		super();
		this.subSection = subSection;
		this.countUnAnswered = countUnAnswered;
	}

	public String getSubSection() {
		return subSection;
	}

	public void setSubSection(String subSection) {
		this.subSection = subSection;
	}

	public Long getCountUnAnswered() {
		return countUnAnswered;
	}

	public void setCountUnAnswered(Long countUnAnswered) {
		this.countUnAnswered = countUnAnswered;
	}
	
	

}
