package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.v2.competency.management.entities.Category;

public interface CategoryRepo extends CrudRepository<Category, Long> {
	
	

	 @Query("select o from Category o where  o.category =:category and o.subCategory =:subCategory and o.competency =:competency and o.companyId =:companyId and o.parentCompetency=:parentCompetency and o.assessmentName=:assessmentName ")
	 public Category findUniquerRecord(@Param("category")  String category, @Param("subCategory")  String subCategory, @Param("competency")  String competency,
			 @Param("parentCompetency")  String parentCompetency, @Param("assessmentName")  String assessmentName,
			 @Param("companyId")  String companyId);
	 
	 @Query("select distinct(o.category) from Category o where  o.companyId =:companyId order by o.category ASC")
	 public List<String> findDistictCategories(@Param("companyId")  String companyId);
	 
	 @Query("select o from Category o where  o.companyId =:companyId and o.category =:category order by o.subCategory ASC")
	 public List<Category> findSubCategories(@Param("companyId")  String companyId, @Param("category")  String category);

}
