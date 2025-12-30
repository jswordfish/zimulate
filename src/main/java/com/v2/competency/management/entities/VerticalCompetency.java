package com.v2.competency.management.entities;

import javax.persistence.Entity;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerticalCompetency extends Base{
	
	@ExcelCellName(value = "Vertical")
	String vertical;
	
	@ExcelCellName(value = "Competency")
	String competency;
	
	
	@ExcelCellName(value = "Vertical Description")
	String verticalDesc;


	public String getVertical() {
		return vertical;
	}


	public void setVertical(String vertical) {
		this.vertical = vertical;
	}


	public String getCompetency() {
		return competency;
	}


	public void setCompetency(String competency) {
		this.competency = competency;
	}


	public String getVerticalDesc() {
		return verticalDesc;
	}


	public void setVerticalDesc(String verticalDesc) {
		this.verticalDesc = verticalDesc;
	}
	
	

}
