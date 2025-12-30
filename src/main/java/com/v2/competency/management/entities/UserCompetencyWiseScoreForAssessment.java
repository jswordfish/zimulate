package com.v2.competency.management.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompetencyWiseScoreForAssessment extends Base{

	@ManyToOne
	AssessmentMapper assessmentMapper;
	
	String email;
	
	String testName;
	
	String testIdentifier;
	
	String competency;
	
	String parentCompetency;
	
	Integer attempt;
	
	@Builder.Default
	String questionMode = Question_Type.MCQ.type;
	
	Float averageScore;
	
	/**
	 * applicable in case of subjective questions
	 */
	Float aiScore;
	
	/**
	 * if applicable
	 */
	String aiOverAllComments;
	
	/**
	 * if applicable
	 */
	String aiAreaOfImprovements;
	
	 
	
	/**
	 * if applicable
	 */
	Float reviewerScore;
	
	String reviewerOverAllComments;
	
	String reviewerAreaOfImprovements;
	
	String transcript;
	
	/**
	 * if applicable
	 */
	String audioVideoLink;
	
	@Builder.Default
	Boolean evaluationFailed = false;
	
	/**
	 * Comma separate answer ids
	 */
	String answerIds;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(attempt, competency, email, parentCompetency, questionMode, testIdentifier, testName, getCompanyId());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserCompetencyWiseScoreForAssessment other = (UserCompetencyWiseScoreForAssessment) obj;
		return Objects.equals(attempt, other.attempt) && Objects.equals(competency, other.competency)
				&& Objects.equals(email, other.email) && Objects.equals(parentCompetency, other.parentCompetency)
				&& Objects.equals(questionMode, other.questionMode)
				&& Objects.equals(testIdentifier, other.testIdentifier) && Objects.equals(testName, other.testName)
				&& Objects.equals(companyId, other.companyId);
	}

	
	
	
}
