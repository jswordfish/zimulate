package com.v2.competency.management.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

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
public class AssessmentMapper extends Base{
	
	String email;
	
	String firstName;
	
	String lastName;
	
	Boolean external;
	
	String testName;
	
	String testIdentifier;
	
	String path1;
	
	String path2;
	
	String path3;
	
	String path4;
	
	String path5;
	
	@Column(length = 2000)
	String assignmentOverview;
	
	
	String testLink;
	
	@Transient
	String testGivenByUser;
	
	
	String typePath1;
	
	String typePath2;
	
	String typePath3;
	
	String typePath4;
	
	String typePath5;
	
	@Builder.Default
	Boolean reviewMode = false;
	
	String reviewedUser;
	
	String reviewedUserEmail;
	
	Float scorePercent;
	
	Boolean consolidatedAssessments;
	
	@Builder.Default
	Boolean aiInsightsGenerated = false;
	
	@Builder.Default
	Boolean humanReviewDone = false;
	
	String assignedBy;
	
	String aiPersonaType;
	
}
