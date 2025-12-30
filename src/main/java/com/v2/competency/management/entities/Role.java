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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends Base{

	
	String roleName;
	
	@Column(length = 6000)
	String roleDesc;
	
	String imageUrl;
	
	@Builder.Default
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_COMPETENCIES", joinColumns = @JoinColumn(name = "ROLE_ID"), inverseJoinColumns = @JoinColumn(name = "COMPETENCY_ID"))
	Set<Competency>  competencies = new HashSet<>();

	@Lob
	String competenciesWithProficiency;
	
	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	public String getRoleDesc() {
		return roleDesc;
	}


	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
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
	
	@Override
	public int hashCode() {
		return (this.getRoleName()+this.getCompanyId()).hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof Role)) {
			return false;
		}
		
		Role role = (Role) object;
		if(this.hashCode() == role.hashCode()) {
			return true;
		}
		
	return false;
	}


	public String getCompetenciesWithProficiency() {
		return competenciesWithProficiency;
	}


	public void setCompetenciesWithProficiency(String competenciesWithProficiency) {
		this.competenciesWithProficiency = competenciesWithProficiency;
	}
	
	
	
}
