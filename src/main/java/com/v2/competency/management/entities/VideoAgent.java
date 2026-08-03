package com.v2.competency.management.entities;

import javax.persistence.Entity;

import com.v2.competency.management.dtos.AgentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class VideoAgent extends Base{
	
	String name;
	
	String description;
	
	String objective;
	
	String url;
	
	String industry;
	
	String agentType = AgentType.SALES.getType();
	
	String kbId;
	
	String openingStatement;
	
	String prompt;
	
	
	String videoAgentForCompany;
	
	String products;
	
	String company;
	
	String image;
	
	Boolean dynamicallyCreated;
	
	Boolean dummy;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}

	public String getKbId() {
		return kbId;
	}

	public void setKbId(String kbId) {
		this.kbId = kbId;
	}

	public String getOpeningStatement() {
		return openingStatement;
	}

	public void setOpeningStatement(String openingStatement) {
		this.openingStatement = openingStatement;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getDynamicallyCreated() {
		return dynamicallyCreated;
	}

	public void setDynamicallyCreated(Boolean dynamicallyCreated) {
		this.dynamicallyCreated = dynamicallyCreated;
	}

	public Boolean getDummy() {
		return dummy;
	}

	public void setDummy(Boolean dummy) {
		this.dummy = dummy;
	}
	
	
	
}
