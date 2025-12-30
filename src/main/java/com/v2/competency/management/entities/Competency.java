package com.v2.competency.management.entities;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.poiji.annotation.ExcelCellName;

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
public class Competency extends Base{
	
	@ExcelCellName(value = "Level")
	String level;
	
	@ExcelCellName(value = "Competency")
	String competency;
	
	@ExcelCellName(value = "Parent Competency")
	String parentCompetency;
	
	
	@ExcelCellName(value = "Competency Description")
	@Lob
	String competencyDesc;
	
	@ExcelCellName("Level 1 Expectations")
	@Lob
	String level1;
	
	@ExcelCellName("Level 2 Expectations")
	@Lob
	String level2;
	
	@ExcelCellName("Level 3 Expectations")
	@Lob
	String level3;
	
	@ExcelCellName("Level 4 Expectations")
	@Lob
	String level4;
	
	@ExcelCellName("Level 5 Expectations")
	@Lob
	String level5;
	
	@ExcelCellName("Level 6 Expectations")
	@Lob
	String level6;
	
	//only while uploading role
	@Transient
	String proficiency;
	
	@Override
	public int hashCode() {
		if(getParentCompetency() != null) {
			return (getCompetency()+getParentCompetency()+getCompanyId()).hashCode();
		}
		else {
			return (getCompetency()+getCompanyId()).hashCode();
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof Competency)) {
			return false;
		}
		
		Competency competency2 = (Competency) object;
		return this.hashCode() == competency2.hashCode();
	}
	
	
	
}
