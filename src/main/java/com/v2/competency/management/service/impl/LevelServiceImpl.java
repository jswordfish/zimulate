package com.v2.competency.management.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.v2.competency.management.entities.Level;
import com.v2.competency.management.repos.LevelRepo;
import com.v2.competency.management.service.LevelService;
@Service
@Transactional
public class LevelServiceImpl implements LevelService{
	
	@Autowired
	LevelRepo levelRepo;
	
	Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public Level findByLevelName(String levelName) {
		// TODO Auto-generated method stub
		return levelRepo.findByLevelName(levelName);
	}

	@Override
	public List<Level> findChildren(Long parent_id) {
		// TODO Auto-generated method stub
		return levelRepo.findChildren(parent_id);
	}

	@Override
	public synchronized Level saveOrUpdate(Level level) {
		// TODO Auto-generated method stub
		Level level2 = findByLevelName(level.getLevelName());
		Level parent =  level.getParent();
		if(parent != null) {
			Level parent2 = findByLevelName(parent.getLevelName());
			if(parent2 == null) {
				parent.setCreateDate(new Date());
				parent = levelRepo.save(parent);
			}
			else {
				parent = parent2;
			}
		}
		level.setParent(parent);
			if(level2 == null) {
				//create
				level.setCreateDate(new Date());
				//level.getChildLevels().clear();
				
				level = levelRepo.save(level);
				List<Level> childs = saveChildren(level.getChildLevels(), level);
				
				//level.getChildLevels().addAll(childs);
				
				level = levelRepo.save(level);
				return level;
				
			}
			else {
				//level.setId(level2.getId());
				//level.setCreateDate(level2.getCreateDate());
				level2.setUpdateDate(new Date());
				
				List<Level> childs = saveChildren(level.getChildLevels(), level2);
				level2.getChildLevels().clear();
				level2.getChildLevels().addAll(childs);
				level2.setParent(parent);
				levelRepo.save(level2);
				//mapper.map(level, level2);
				
			}
		return level2;
	}
	
	private void save() {
		
	}
	
	private List<Level> saveChildren(List<Level> children, Level current) {
		List<Level> children2 = new ArrayList<Level>();
		for(Level level : children) {
			level.setParent(current);
			Level level2 = findByLevelName(level.getLevelName());
			if(level2 == null) {
				level.setCreateDate(new Date());
				level2 = levelRepo.save(level);
			}
			children2.add(level2);
		}
		return children2;
	}

}
