package com.googlecloud.vertex.ai.roleplay.insights.dto.newversion;

import java.util.ArrayList;
import java.util.List;

import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;

public class SectionResult {
	
	Section section;
	
	List<RoleplayInsightsDetail> list = new ArrayList<>();
	
	

	public SectionResult(Section section, List<RoleplayInsightsDetail> list) {
		super();
		this.section = section;
		this.list = list;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public List<RoleplayInsightsDetail> getList() {
		return list;
	}

	public void setList(List<RoleplayInsightsDetail> list) {
		this.list = list;
	}
	
	

}
