package com.v2.competency.management.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDescription extends Base {
	
	
String jobDescName;
	
	@Column(length = 3000)
	String description;
	
	String imageUrl;
	
	@Builder.Default
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_COMPETENCIES", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "COMPETENCY_ID"))
	Set<Competency>  competencies = new HashSet<>();
	
	@Lob
	String competenciesWithProficiency;

	public String getJobDescName() {
		return jobDescName;
	}

	public void setJobDescName(String jobDescName) {
		this.jobDescName = jobDescName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Set<Competency> getCompetencies() {
		return competencies;
	}

	public void setCompetencies(Set<Competency> competencies) {
		this.competencies = competencies;
	}

	public String getCompetenciesWithProficiency() {
		return competenciesWithProficiency;
	}

	public void setCompetenciesWithProficiency(String competenciesWithProficiency) {
		this.competenciesWithProficiency = competenciesWithProficiency;
	}

	//@Lob
	//String competenciesWithProficiency;
	
	

}
