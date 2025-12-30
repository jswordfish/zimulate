package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.VerticalCompetency;

public interface VerticalRepo extends CrudRepository<VerticalCompetency, Long>  {
	
	 @Query("select DISTINCT o.vertical from VerticalCompetency o where  o.companyId =:companyId")
	public List<String> findVerticalsForCompanyId(String companyId);
	 
	 
	 @Query("select o from VerticalCompetency o where o.vertical=:vertical and  o.competency =:competency and o.companyId =:companyId")
	 public VerticalCompetency findByCompetency(@Param("vertical")  String vertical, @Param("competency")  String competency, String companyId);
	 
	 
	 @Query("select o from VerticalCompetency o where o.vertical=:vertical and o.companyId =:companyId")
	 public List<VerticalCompetency> findCompetenciesForVertical(@Param("vertical")  String vertical, String companyId);
	 
	 
}
