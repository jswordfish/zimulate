package com.v2.competency.management.dtos;

import java.util.List;

import com.v2.competency.management.entities.AssessmentMapper;

public class Survey360DegreeScoreDto {
	
	AssessmentMapper self;
	
	List<AssessmentMapper> reviewers;
	
	Float averageScore;

	public AssessmentMapper getSelf() {
		return self;
	}

	public void setSelf(AssessmentMapper self) {
		this.self = self;
	}

	public List<AssessmentMapper> getReviewers() {
		return reviewers;
	}

	public void setReviewers(List<AssessmentMapper> reviewers) {
		this.reviewers = reviewers;
	}

	public Float getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Float averageScore) {
		this.averageScore = averageScore;
	}
	
	

}
