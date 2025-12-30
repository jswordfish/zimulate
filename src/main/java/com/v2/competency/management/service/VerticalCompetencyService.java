package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.VerticalCompetency;

public interface VerticalCompetencyService {
	
	public List<String> findVerticalsForCompanyId(String companyId);
	 
	 
	 public VerticalCompetency findByCompetency(String vertical,  String competency, String companyId);
	 
	 
	 public List<VerticalCompetency> findCompetenciesForVertical( String vertical, String companyId);
	 
	 public VerticalCompetency saveOrUpdate(VerticalCompetency verticalCompetency);
	
}
