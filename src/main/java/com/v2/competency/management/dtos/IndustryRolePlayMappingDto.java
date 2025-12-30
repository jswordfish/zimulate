package com.v2.competency.management.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.v2.competency.management.entities.VFRolePlayTest;

public class IndustryRolePlayMappingDto {
	
	
	String industry;
	
	List<RolePlayTestDto> roleplays = new ArrayList<>();
	
	
	
	

	public IndustryRolePlayMappingDto(String industry) {
		super();
		this.industry = industry;
	}

	public IndustryRolePlayMappingDto(String industry, List<RolePlayTestDto> roleplays) {
		super();
		this.industry = industry;
		this.roleplays = roleplays;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public List<RolePlayTestDto> getRoleplays() {
		return roleplays;
	}

	public void setRoleplays(List<RolePlayTestDto> roleplays) {
		this.roleplays = roleplays;
	}

	@Override
	public int hashCode() {
		return Objects.hash(industry);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndustryRolePlayMappingDto other = (IndustryRolePlayMappingDto) obj;
		return Objects.equals(industry, other.industry);
	}
	
	

}
