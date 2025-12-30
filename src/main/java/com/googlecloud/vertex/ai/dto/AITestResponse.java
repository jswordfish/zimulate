package com.googlecloud.vertex.ai.dto;

import java.util.List;

import com.googlecloud.vertex.ai.insights.dto.InsightsDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AITestResponse {
	
	String transcript;
	
	ExpectedResponse2 response;
	
	InsightsDto insightsResponse;
	
	List<VFTestUserQuestionAnswer> quesAnswers;

}
