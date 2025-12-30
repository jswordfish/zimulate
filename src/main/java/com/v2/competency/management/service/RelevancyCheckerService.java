package com.v2.competency.management.service;

import java.io.IOException;

import com.googlecloud.vertex.ai.dto.RelevancyScoreForAnswerDto;
import com.v2.competency.management.entities.VFTestUserQuestionAnswer;

public interface RelevancyCheckerService {
	
	
	
	public RelevancyScoreForAnswerDto checkIfAnswerRelevant(VFTestUserQuestionAnswer answer) throws IOException ;

}
