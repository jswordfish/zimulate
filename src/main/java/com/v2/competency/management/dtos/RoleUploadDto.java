package com.v2.competency.management.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder

@NoArgsConstructor
@AllArgsConstructor
public class RoleUploadDto {
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getParentCompetency() {
		return parentCompetency;
	}

	public void setParentCompetency(String parentCompetency) {
		this.parentCompetency = parentCompetency;
	}

	public String getCompetency() {
		return competency;
	}

	public void setCompetency(String competency) {
		this.competency = competency;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getProficiency() {
		return proficiency;
	}

	public void setProficiency(String proficiency) {
			try {
				if(Proficiency.valueOf(proficiency) != null) {
					this.proficiency = proficiency;
				}
				else{
					throw new RuntimeException("Invalid Proficiency string "+proficiency);
				}
			} catch (Exception e) {
				throw new RuntimeException("Invalid Proficiency string "+proficiency);
			}
		
	}

	@ExcelCellName("Role")
	String roleName;
	
	@ExcelCellName("Competency")
	String parentCompetency;
	
	@ExcelCellName("Sub-Competency")
	String competency;
	
	@ExcelCellName("Role Description")
	String roleDesc;
	
	@ExcelCellName("Company ID")
	String companyId;
	
	@ExcelCellName("Proficiency Level")
	String proficiency;
	
	

}
