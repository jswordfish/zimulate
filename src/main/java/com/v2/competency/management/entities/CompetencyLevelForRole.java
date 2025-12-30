package com.v2.competency.management.entities;

import java.util.Objects;

import javax.persistence.Entity;

import com.v2.competency.management.dtos.Proficiency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyLevelForRole extends Base{
	
	String  roleName;
	
	String competency;
	
	Long roleId;
	
	Long competencyId;
	
	String parentCompetency;
	
	
	@Builder.Default
	String proficiency = Proficiency.LEVEL1.getLevel();


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(competency, parentCompetency, roleName,companyId);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompetencyLevelForRole other = (CompetencyLevelForRole) obj;
		return Objects.equals(competency, other.competency) && Objects.equals(parentCompetency, other.parentCompetency)
				&& Objects.equals(roleName, other.roleName) && Objects.equals(companyId, other.companyId);
	}
	
	

	
}
