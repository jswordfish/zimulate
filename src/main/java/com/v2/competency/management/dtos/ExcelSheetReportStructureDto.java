package com.v2.competency.management.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelSheetReportStructureDto {
	
	List<String> headers;
	
	List<List<Object>> data;

	
	
	

}
