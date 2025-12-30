package com.v2.competency.management.service;

import java.util.List;

import com.v2.competency.management.entities.Level;

public interface LevelService {
	
	public Level findByLevelName(String levelName);
	 
	List<Level> findChildren(Long parent_id);
	 
	public Level saveOrUpdate(Level level);

}
