package com.v2.competency.management.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class RolePlayQuestionAnswer extends Base {
	
String email;
	
	String firstName;
	
	String lastName;
	
	String testName;
	
	String aiAnalysisLink;
	
	Integer attempt;
	
	@Column(length = 2000)
	String question;
	
	@Column(length = 7000)
	String answer;
	
	@Enumerated(EnumType.STRING)  
    private RolePlayQuestionFollowUpLevel followUpLevel;
	
	private String difficultyLevel = RoleplayDifficultyLevel.EASY.getLevel();
	
	private String rolePlayPersona;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	

	public String getAiAnalysisLink() {
		return aiAnalysisLink;
	}

	public void setAiAnalysisLink(String aiAnalysisLink) {
		this.aiAnalysisLink = aiAnalysisLink;
	}

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public RolePlayQuestionFollowUpLevel getFollowUpLevel() {
		return followUpLevel;
	}

	public void setFollowUpLevel(RolePlayQuestionFollowUpLevel followUpLevel) {
		this.followUpLevel = followUpLevel;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getDifficultyLevel() {
		return difficultyLevel;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public String getRolePlayPersona() {
		return rolePlayPersona;
	}

	public void setRolePlayPersona(String rolePlayPersona) {
		this.rolePlayPersona = rolePlayPersona;
	}
	
	
	
	

}
