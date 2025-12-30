package com.v2.competency.management.dtos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.v2.competency.management.entities.AssessmentMapper;

public class PaginatedResponseDto {
	
	Integer recordsFrom;
	
	Integer recordsTo;
	
	Integer totalNumberOfRecords;
	
	Integer totalNumberOfPages;
	
	Integer selectedPage;
	
	//Integer previousPage;
	
	//Integer nextPage;
	
	List<? extends Object> list = new ArrayList<>();
	
	Map<String, List<AssessmentMapper>> map = new HashMap<>();

	public Integer getRecordsFrom() {
		return recordsFrom;
	}

	public void setRecordsFrom(Integer recordsFrom) {
		this.recordsFrom = recordsFrom;
	}

	public Integer getRecordsTo() {
		return recordsTo;
	}

	public void setRecordsTo(Integer recordsTo) {
		this.recordsTo = recordsTo;
	}

	public Integer getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Integer totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}

	public Integer getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(Integer selectedPage) {
		this.selectedPage = selectedPage;
	}

	

	public List<? extends Object> getList() {
		return list;
	}

	public void setList(List<? extends Object> list) {
		this.list = list;
	}

	public Map<String, List<AssessmentMapper>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<AssessmentMapper>> map) {
		this.map = map;
	}

	
	
	

}
