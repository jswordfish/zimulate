package com.v2.competency.management.entities;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecloud.vertex.ai.roleplay.insights.dto.RolePlayInsightsDto;
import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class VFTestUserQuestionAnswer extends Base{
	
	String email;
	
	String firstName;
	
	String lastName;
	
	String testIdentifier;
	
	String aiAnalysisLink;
	
	Integer attempt;
	
	@Column(length = 2000)
	String question;
	
	@Column(length = 7000)
	String answer;
	
	@Column
	String answerChoiceIncaseOfMCQ;
	
	String questionType;
	
	Integer scoreInPercent;
	
	Integer timeTakenToAnswerInMinutes;
	
	
	//String reviewer1Response;
	
	String reviewer1Email;
	
	String reviewer1FullName;
	
	
	@Lob
	String analysis;
	
	@Transient
	Boolean last;
	
	@Transient
	Long timeOfLastQuestion;
	
	String qid;
	
	String competency;
	

	String parentCompetency;
	
	String answerAudioOrVideo;
	
	String videoLink;
	
	@Transient
	Question q;
	
	/**
	 * incase of MCQ
	 */
	Boolean markedCorrect = false;
	
	Float overallScoreIncaseOfSubjective;
	
	Boolean aiInsightsGenerated = false;
	
	Boolean reviewerInsightsGenerated = false;
	
	Float overallScoreIncaseOfSubjectiveByReviewer;
	
	@Lob
	String aiAnalysisJson;
	
	@Lob
	String reviewerAnalysisJson;
	
	@Lob
	String customAiAnalysisJson;
	
	@Transient
	RolePlayInsightsDto customcustomAiAnalysis;
	
	@Transient
	ObjectMapper mapper;

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getTestIdentifier() {
		return testIdentifier;
	}


	public void setTestIdentifier(String testIdentifier) {
		this.testIdentifier = testIdentifier;
	}


	public String getAiAnalysisLink() {
		return aiAnalysisLink;
	}


	public void setAiAnalysisLink(String aiAnalysisLink) {
		this.aiAnalysisLink = aiAnalysisLink;
	}


	public Integer getAttempt() {
		return attempt;
	}


	public void setAttempt(Integer attempt) {
		this.attempt = attempt;
	}


	public String getQuestion() {
		return question;
	}


	public void setQuestion(String question) {
		this.question = question;
	}


	public String getAnswer() {
		return answer;
	}


	public void setAnswer(String answer) {
		this.answer = answer;
	}


	public Integer getTimeTakenToAnswerInMinutes() {
		return timeTakenToAnswerInMinutes;
	}


	public void setTimeTakenToAnswerInMinutes(Integer timeTakenToAnswerInMinutes) {
		this.timeTakenToAnswerInMinutes = timeTakenToAnswerInMinutes;
	}


	public String getAnalysis() {
		return analysis;
	}


	public void setAnalysis(String analysis) {
		this.analysis = analysis;
	}


	public Boolean getLast() {
		return last;
	}


	public void setLast(Boolean last) {
		this.last = last;
	}


	public Long getTimeOfLastQuestion() {
		return timeOfLastQuestion;
	}


	public void setTimeOfLastQuestion(Long timeOfLastQuestion) {
		this.timeOfLastQuestion = timeOfLastQuestion;
	}


	public String getQid() {
		return qid;
	}


	public void setQid(String qid) {
		this.qid = qid;
	}


	public String getQuestionType() {
		return questionType;
	}


	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}


	public Integer getScoreInPercent() {
		
		return scoreInPercent;
	}


	public void setScoreInPercent(Integer scoreInPercent) {
		this.scoreInPercent = scoreInPercent;
	}


	


	public String getReviewer1Email() {
		return reviewer1Email;
	}


	public void setReviewer1Email(String reviewer1Email) {
		this.reviewer1Email = reviewer1Email;
	}


	public String getReviewer1FullName() {
		return reviewer1FullName;
	}


	public void setReviewer1FullName(String reviewer1FullName) {
		this.reviewer1FullName = reviewer1FullName;
	}


	public String getCompetency() {
		return competency;
	}


	public void setCompetency(String competency) {
		this.competency = competency;
	}


	public String getParentCompetency() {
		return parentCompetency;
	}


	public void setParentCompetency(String parentCompetency) {
		this.parentCompetency = parentCompetency;
	}


	public String getAnswerAudioOrVideo() {
		return answerAudioOrVideo;
	}


	public void setAnswerAudioOrVideo(String answerAudioOrVideo) {
		this.answerAudioOrVideo = answerAudioOrVideo;
	}
	
	


	public String getAnswerChoiceIncaseOfMCQ() {
		return answerChoiceIncaseOfMCQ;
	}


	public void setAnswerChoiceIncaseOfMCQ(String answerChoiceIncaseOfMCQ) {
		this.answerChoiceIncaseOfMCQ = answerChoiceIncaseOfMCQ;
	}


	public Question getQ() {
		return q;
	}


	public void setQ(Question q) {
		this.q = q;
	}


	public Boolean getMarkedCorrect() {
		return markedCorrect;
	}


	public void setMarkedCorrect(Boolean markedCorrect) {
		this.markedCorrect = markedCorrect;
	}


	public Float getOverallScoreIncaseOfSubjective() {
		return overallScoreIncaseOfSubjective;
	}


	public void setOverallScoreIncaseOfSubjective(Float overallScoreIncaseOfSubjective) {
		this.overallScoreIncaseOfSubjective = overallScoreIncaseOfSubjective;
	}


	public Boolean getAiInsightsGenerated() {
		return aiInsightsGenerated;
	}


	public void setAiInsightsGenerated(Boolean aiInsightsGenerated) {
		this.aiInsightsGenerated = aiInsightsGenerated;
	}


	public Boolean getReviewerInsightsGenerated() {
		return reviewerInsightsGenerated;
	}


	public void setReviewerInsightsGenerated(Boolean reviewerInsightsGenerated) {
		this.reviewerInsightsGenerated = reviewerInsightsGenerated;
	}


	public String getAiAnalysisJson() {
		return aiAnalysisJson;
	}


	public void setAiAnalysisJson(String aiAnalysisJson) {
		this.aiAnalysisJson = aiAnalysisJson;
	}


	public String getReviewerAnalysisJson() {
		return reviewerAnalysisJson;
	}


	public void setReviewerAnalysisJson(String reviewerAnalysisJson) {
		this.reviewerAnalysisJson = reviewerAnalysisJson;
	}


	public Float getOverallScoreIncaseOfSubjectiveByReviewer() {
		return overallScoreIncaseOfSubjectiveByReviewer;
	}


	public void setOverallScoreIncaseOfSubjectiveByReviewer(Float overallScoreIncaseOfSubjectiveByReviewer) {
		this.overallScoreIncaseOfSubjectiveByReviewer = overallScoreIncaseOfSubjectiveByReviewer;
	}


	public String getCustomAiAnalysisJson() {
		return customAiAnalysisJson;
	}


	public void setCustomAiAnalysisJson(String customAiAnalysisJson) {
		this.customAiAnalysisJson = customAiAnalysisJson;
	}


	public RolePlayInsightsDto getCustomcustomAiAnalysis() {
//		if(this.getCustomAiAnalysisJson() != null && this.getCustomAiAnalysisJson().trim().length() > 0) {
//			try {
//				return mapper.readValue(this.getCustomAiAnalysisJson().getBytes(), RolePlayInsightsDto.class);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//				return null;
//			}
//		}
		return customcustomAiAnalysis;
	}


	public void setCustomcustomAiAnalysis(RolePlayInsightsDto customcustomAiAnalysis) {
		this.customcustomAiAnalysis = customcustomAiAnalysis;
	}


	public String getVideoLink() {
		return videoLink;
	}


	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}
	
	
	
}
