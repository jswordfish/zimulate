package com.v2.competency.management.dtos;

import java.util.Objects;

import com.v2.competency.management.entities.Competency;

public class CompetencyWithProficiencyForRole {
	
	Long competencyId;
	
	String competency;
	
	String parentCompetency;
	
	String proficiency = Proficiency.LEVEL1.getLevel();

	public Long getCompetencyId() {
		return competencyId;
	}

	public void setCompetencyId(Long competencyId) {
		this.competencyId = competencyId;
	}

	public String getCompetency() {
		return competency;
	}

	public void setCompetency(String competency) {
		this.competency = competency;
	}

	public String getParentCompetency() {
		return parentCompetency;
	}

	public void setParentCompetency(String parentCompetency) {
		this.parentCompetency = parentCompetency;
	}

	public String getProficiency() {
		return proficiency;
	}

	public void setProficiency(String proficiency) {
		this.proficiency = proficiency;
	}

	@Override
	public int hashCode() {
		return Objects.hash(competency, parentCompetency);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompetencyWithProficiencyForRole other = (CompetencyWithProficiencyForRole) obj;
		return Objects.equals(competency, other.competency) && Objects.equals(parentCompetency, other.parentCompetency);
	}
	
	

}
