package com.googlecloud.vertex.ai.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpectedResponse2 {
	
	List<VertexResponse> list = Arrays.asList(new VertexResponse());
	
	FinalSummary finalSummary = new FinalSummary();

	public List<VertexResponse> getList() {
		return list;
	}

	public void setList(List<VertexResponse> list) {
		this.list = list;
	}

	public FinalSummary getFinalSummary() {
		return finalSummary;
	}

	public void setFinalSummary(FinalSummary finalSummary) {
		this.finalSummary = finalSummary;
	}
	
	

}
