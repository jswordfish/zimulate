package com.v2.competency.management.util;

import java.util.ArrayList;
import java.util.List;

import com.v2.competency.management.dtos.InitRolePlayTestDTO.CompetitionInfoDTO;
import com.v2.competency.management.dtos.InitRolePlayTestDTO.ProductInfoDTO;
import com.v2.competency.management.dtos.InitRolePlayTestDTO.QuestionTextDTO;

public class RolePlayParser {
	
	public static QuestionTextDTO parseQuestionText(String questionText) {
        QuestionTextDTO dto = new QuestionTextDTO();
        
        for (String line : questionText.split("\n")) {
            if (line.startsWith("Your Role:")) {
                dto.setYourRole(line.replace("Your Role:", "").trim());
            } else if (line.startsWith("Product to Sell:")) {
                dto.setProductToSell(line.replace("Product to Sell:", "").trim());
            }
            else if(line.startsWith("Products to Understand:") ) {
            	dto.setProductToSell(line.replace("Products to Understand:", "").trim());
            }
            else if(line.startsWith("Product to Understand:")) {
            	dto.setProductToSell(line.replace("Product to Understand:", "").trim());
            }
            else if (line.startsWith("Your Goal:")) {
                dto.setYourGoal(line.replace("Your Goal:", "").trim());
            }
        }
        return dto;
    }

    public static ProductInfoDTO parseProductInfo(String productInfo) {
        ProductInfoDTO dto = new ProductInfoDTO();
        String[] lines = productInfo.split("\n");
        
        if (lines.length > 0) dto.setTitle(lines[0].replace("•", "").trim());
        if (lines.length > 1) dto.setLink(lines[1].replace("•", "").trim());
        dto.setDesc("...."); // placeholder or parse extra if available
        return dto;
    }

    public static List<CompetitionInfoDTO> parseCompetitionInfo(String competitionInfo) {
        List<CompetitionInfoDTO> list = new ArrayList<>();
        String[] lines = competitionInfo.split("\n•");
        
        for (String block : lines) {
            if (block.trim().isEmpty()) continue;
            
            String[] parts = block.split("–");
            CompetitionInfoDTO dto = new CompetitionInfoDTO();
            dto.setTitle(parts[0].trim());
            dto.setLink(parts.length > 1 ? parts[1].trim() : "");
            dto.setDesc("...."); // placeholder
            list.add(dto);
        }
        return list;
    }

}
