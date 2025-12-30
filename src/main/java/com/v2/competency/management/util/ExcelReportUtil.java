package com.v2.competency.management.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.insights.dto.InsightForScenarioBasedQuestion;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RoleplayInsightsDetail;
import com.v2.competency.management.dtos.ExcelSheetReportStructureDto;
import com.v2.competency.management.entities.Question;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;
import com.v2.competency.management.service.QuestionService;

public class ExcelReportUtil {
	
	static ObjectMapper mapper = new ObjectMapper();
	
	public static XSSFWorkbook createExcelReport(String testName, List<String> headers, Map<Integer, List<Object>> data, String fileName, XSSFWorkbook workbook, String sheetName) {
		try {
			if(workbook == null) {
				workbook=new XSSFWorkbook();
			}
			XSSFSheet sheet=workbook.createSheet(sheetName);
			XSSFRow row=sheet.createRow(0);
			for(int i=0;i<headers.size();i++) {
				row.createCell(i).setCellValue(headers.get(i));
			}
			sheet.createFreezePane(	0, 1);
			XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
			cellStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.index);
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			row.setRowStyle(cellStyle);
			
			for(Integer rowNum:data.keySet()) {
				XSSFRow rowData=sheet.createRow(rowNum);
				List<Object> rowValues = data.get(rowNum);
				for(int i=0;i<rowValues.size();i++) {
					rowData.createCell(i).setCellValue(rowValues.get(i).toString());
				}
			}
			
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	        workbook.write(outputStream);
//	        workbook.close();
//	        return outputStream.toByteArray();
			return workbook;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}
	
	public static XSSFWorkbook createExcelReport( Map<String, ExcelSheetReportStructureDto> all_data_with_headers) {
		try {
			XSSFWorkbook workbook=new XSSFWorkbook();
				for(String sheetName : all_data_with_headers.keySet()) {
					XSSFSheet sheet=workbook.createSheet(sheetName);
					XSSFRow row=sheet.createRow(0);
					ExcelSheetReportStructureDto all = all_data_with_headers.get(sheetName);
					List<String> headers =  all.getHeaders();
					for(int i=0;i<headers.size();i++) {
						XSSFCell cell =  row.createCell(i);
						cell.setCellValue(headers.get(i));
						XSSFCellStyle cellStyleI = sheet.getWorkbook().createCellStyle();
						cellStyleI.setWrapText(true);
						//sheet.setau
					}
					sheet.createFreezePane(	0, 1);
					XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
					cellStyle.setFillBackgroundColor(IndexedColors.GREY_50_PERCENT.index);
					cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					//cellStyle.setWrapText(true); 
					row.setRowStyle(cellStyle);
					
					List<List<Object>> data =  all.getData();
					Integer dataRow = 1;
					for(List<Object> rowLevelData : data) {
						XSSFRow rowData=sheet.createRow(dataRow);
						int column = 0;
						for(Object cellLevelData : rowLevelData) {
							if(cellLevelData != null) {
								XSSFCell rowcell = rowData.createCell(column);
								rowcell.setCellValue(cellLevelData.toString());
								XSSFCellStyle cellStyleR = sheet.getWorkbook().createCellStyle();
								cellStyleR.setWrapText(true);
								column++;
							}
							
						}
						dataRow++;
					}
				}
			
			
			
			
			
			
			
			return workbook;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}
	
	
	public static Map<String, ExcelSheetReportStructureDto> classifyScenarioAnswers(List<VFTestUserQuestionAnswer> answers, QuestionService questionService) throws JsonParseException, JsonMappingException, IOException{
		
		Map<String, Set<String>> headers = new HashMap<>();
		Map<Set<String>, List<VFTestUserQuestionAnswer>> headers_data_map = new HashMap();
		Integer count = 1;
				for(VFTestUserQuestionAnswer quest : answers) {
					Set<String> scenarioHeaders = new HashSet();
					scenarioHeaders.add("First Name");
					scenarioHeaders.add("Last Name");
					scenarioHeaders.add("Email");
					scenarioHeaders.add("Scenario");
					scenarioHeaders.add("Text Response");
					scenarioHeaders.add("Audio Response");
					Question q = questionService.findById(Long.parseLong(quest.getQid()));
					String customJson = quest.getReviewerAnalysisJson() == null?quest.getCustomAiAnalysisJson():quest.getReviewerAnalysisJson();
					if(q.getMultipleCompetenciesAssociatedWithQuestion() != null && customJson != null) {
						
						RolePlayInsightsDto custom = mapper.readValue(customJson.getBytes(), RolePlayInsightsDto.class);
						
							for(String competency : custom.getMapCompetenciesInsights().keySet()) {
								RoleplayInsightsDetail detail = custom.getMapCompetenciesInsights().get(competency);
								scenarioHeaders.add(competency+"_Observation");
								scenarioHeaders.add(competency+"_Improvement Areas");
								scenarioHeaders.add(competency+"_Score");
							}
						scenarioHeaders.add("Overall Observations");
						scenarioHeaders.add("Overall Score");
						//scenarioHeaders.add(quest.getQuestion());
					}
					else {
						String json = quest.getReviewerAnalysisJson() == null?quest.getAiAnalysisJson():quest.getReviewerAnalysisJson();
						if(json == null) {
							continue;
						}
						InsightForScenarioBasedQuestion insightForScenarioBasedQuestion = mapper.readValue(json, InsightForScenarioBasedQuestion.class);
	    				scenarioHeaders.add("Answer Relevance To Scenario");
	    				scenarioHeaders.add("Problem Solving Skills");
	    				scenarioHeaders.add("Decision Making Skills");
	    				scenarioHeaders.add("Communication Skills");
	    				scenarioHeaders.add("Creativity & Innovation");
	    				scenarioHeaders.add("Ethical Considerations In Answer");
	    				scenarioHeaders.add("Areas of Improvement");
	    				scenarioHeaders.add("Overall Observation");
	    				
	    				scenarioHeaders.add("Score - Answer Relevance To Scenario");
	    				scenarioHeaders.add("Score - Problem Solving Skills");
	    				scenarioHeaders.add("Score - Decision Making Skills");
	    				scenarioHeaders.add("Score - Communication Skills");
	    				
	    				scenarioHeaders.add("Score - Creativity & Innovation");
	    				scenarioHeaders.add("Score - Ethical Considerations In Answer");
	    				scenarioHeaders.add("Overall Score");
					}
					
					boolean distinct = true;
					for(Set<String> element : headers.values()) {
						if(scenarioHeaders.equals(element)) {
							distinct = false;
							break;
						}
					}
					
					if(distinct) {
						headers.put("Scenario Report "+count, scenarioHeaders);
						List<VFTestUserQuestionAnswer> ans = new ArrayList<>();
						ans.add(quest);
						headers_data_map.put(scenarioHeaders, ans);
						count++;
					}
					else {
						headers_data_map.get(scenarioHeaders).add(quest);
					}
					
				}
				
				return process(headers, headers_data_map, questionService);
		}
	
	
	private static Map<String, ExcelSheetReportStructureDto> process(Map<String, Set<String>> headers, Map<Set<String>, List<VFTestUserQuestionAnswer>> headers_data_map, QuestionService questionService) throws JsonParseException, JsonMappingException, IOException{
		Map<String, ExcelSheetReportStructureDto> ret = new HashMap<>();
		for(String keySheetName : headers.keySet()) {
			List<VFTestUserQuestionAnswer> list =  headers_data_map.get(headers.get(keySheetName));
			List<List<Object>> scenarioData = new ArrayList<>();
			for(VFTestUserQuestionAnswer ans : list) {
				if(questionService.findById(Long.parseLong(list.get(0).getQid())).getMultipleCompetenciesAssociatedWithQuestion() != null && (ans.getReviewerAnalysisJson() != null || ans.getCustomAiAnalysisJson() != null)) {
					//for(VFTestUserQuestionAnswer ans : list) {
						
						List<Object> rowLevelDataForEachUser = new ArrayList<>();
						rowLevelDataForEachUser.add(ans.getFirstName());
						rowLevelDataForEachUser.add(ans.getLastName());
						rowLevelDataForEachUser.add(ans.getEmail());
						rowLevelDataForEachUser.add(ans.getQuestion());
						rowLevelDataForEachUser.add(ans.getAnswer());
						rowLevelDataForEachUser.add(ans.getAnswerAudioOrVideo()==null?"NA":ans.getAnswerAudioOrVideo());
						
						String jsonAns = null;
						RolePlayInsightsDto customAns = null;
							if(ans.getReviewerAnalysisJson() != null) {
								jsonAns = ans.getReviewerAnalysisJson();
								customAns = mapper.readValue(jsonAns.getBytes(), RolePlayInsightsDto.class);
							}
							else if(ans.getCustomAiAnalysisJson() != null){
								jsonAns = ans.getCustomAiAnalysisJson();
								customAns = mapper.readValue(jsonAns.getBytes(), RolePlayInsightsDto.class);
							}
						
						 
						
							for(String competency : customAns.getMapCompetenciesInsights().keySet()) {
								RoleplayInsightsDetail detail = customAns.getMapCompetenciesInsights().get(competency);
								rowLevelDataForEachUser.add(detail.getObservation());
								rowLevelDataForEachUser.add(detail.getImprovementAreas());
								rowLevelDataForEachUser.add(detail.getScoreInPercent());
							}
							rowLevelDataForEachUser.add(customAns.getOverAllObservations());
							rowLevelDataForEachUser.add(customAns.getOverAllScoreInPercent());
					scenarioData.add(rowLevelDataForEachUser);
					//}
				}
				else {
					//for(VFTestUserQuestionAnswer ans : list) {
						List<Object> rowLevelDataForEachUser = new ArrayList<>();
						rowLevelDataForEachUser.add(ans.getFirstName());
						rowLevelDataForEachUser.add(ans.getLastName());
						rowLevelDataForEachUser.add(ans.getEmail());
						rowLevelDataForEachUser.add(ans.getQuestion());
						rowLevelDataForEachUser.add(ans.getAnswer());
						rowLevelDataForEachUser.add(ans.getAnswerAudioOrVideo());
						
						String jsonAns = "";
						InsightForScenarioBasedQuestion insightForScenarioBasedQuestionAns =  null;
						if(ans.getReviewerAnalysisJson() != null) {
							jsonAns = ans.getReviewerAnalysisJson();
							insightForScenarioBasedQuestionAns = mapper.readValue(jsonAns, InsightForScenarioBasedQuestion.class);
						}
						else if(ans.getAiAnalysisJson() != null){
							jsonAns = ans.getAiAnalysisJson();
							insightForScenarioBasedQuestionAns = mapper.readValue(jsonAns, InsightForScenarioBasedQuestion.class);
						}
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getAnswerRelevanceToScenario());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getProblemSolvingSkills());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getDecisionMakingSkills());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getCommunicationSkills());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getCreativityAndInnovation());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getEthicalConsiderationsInAnswer());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getAreasOfImprovement());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getOverAllObservations());
						
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForAnswerRelevanceToScenarioInPercentage());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForProblemSolvingSkillsInPercentage());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForDecisionMakingSkillsInPercentage());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForCommunicationSkillsIPercentage());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForCreativityAndInnovation());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getScoreForEthicalConsiderationsInPercentage());
						rowLevelDataForEachUser.add(insightForScenarioBasedQuestionAns.getOverAllScoreInPErcentage());
						scenarioData.add(rowLevelDataForEachUser);
					}
				//}
				
				
			}
			Set<String> hdrs = headers.get(keySheetName);
			ExcelSheetReportStructureDto dataWithHeaders =  ExcelSheetReportStructureDto.builder().data(scenarioData).headers(new ArrayList<>(hdrs)).build();
			ret.put(keySheetName, dataWithHeaders);
		}
		
		return ret;
	}
}
