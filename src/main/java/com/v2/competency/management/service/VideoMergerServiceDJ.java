package com.v2.competency.management.service;

import java.util.List;

public interface VideoMergerServiceDJ {
	
	public String mergeVideos(String videoPath1, String videoPath2);
	
	public String mergeMultipleVideos(List<String> videoPaths, String outputDir);
	
	public String mergeVideosFinal(String companyId, String testName, String email, Integer attempt);

}
