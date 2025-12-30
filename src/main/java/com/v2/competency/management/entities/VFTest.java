package com.v2.competency.management.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionId;

import com.v2.competency.management.dtos.CompetencyTest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VFTest extends Base{
	
	String testIdentifier;
	
	String testName;
	
	
	@Lob
	String testXml;
	
	@Transient
	CompetencyTest competencyTest;
	
	String publicTestUrl;
	
	@Transient
	String uniqueSkills;
	
	Boolean locked;
	
	/**
	 * not needed for test creation
	 */
	@Builder.Default
	String testType = Test_Type.MCQ_SCENARIO.getTestType();
	
	@Column(length = 1000)
	String testObjectives;
	
	@Builder.Default
	String gradingMethodology = Test_Grading.AI_GRADING.getGrading();
	
	Integer duration;
	
	@Builder.Default
	Boolean isPublic = false;
	
	@Builder.Default
	String questionSource = Question_Source.QUESTION_BANK_RANDOM.getSource();
	
	
	/**
	 * not needed for test creation
	 */
	Boolean aiBasedQuestion;
	
	
	@Lob
	String testXmlForKB;
	
	String path1;
	
	String path2;
	
	@Column(length = 500)
	String testOverview;
	
	
	
}
