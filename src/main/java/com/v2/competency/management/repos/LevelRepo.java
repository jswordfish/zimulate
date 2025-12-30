package com.v2.competency.management.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.v2.competency.management.entities.Level;

public interface LevelRepo extends CrudRepository<Level, Long> {

	
	 public Level findByLevelName(String levelName);
	 
	 @Query(nativeQuery = true, value = "select * from Level  where parent_id=:parent_id")
	 List<Level> findChildren(Long parent_id);
}


