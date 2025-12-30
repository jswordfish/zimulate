package com.googlecloud.vertex.ai.communication.dto;

public class Grammar {
	
	private int sentenceConstructionScore;
	  private int vocabulary;
	  private int tensesScore;
	  private int prepositionsScore;
	  
	  private int useOfCorrectPhrasesAndCollocationsScore;
	  private int overallScore;
	  
	  String improvementAreas;

	public int getSentenceConstructionScore() {
		return sentenceConstructionScore;
	}

	public void setSentenceConstructionScore(int sentenceConstructionScore) {
		this.sentenceConstructionScore = sentenceConstructionScore;
	}

	public int getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(int vocabulary) {
		this.vocabulary = vocabulary;
	}

	public int getTensesScore() {
		return tensesScore;
	}

	public void setTensesScore(int tensesScore) {
		this.tensesScore = tensesScore;
	}

	public int getPrepositionsScore() {
		return prepositionsScore;
	}

	public void setPrepositionsScore(int prepositionsScore) {
		this.prepositionsScore = prepositionsScore;
	}

	public int getUseOfCorrectPhrasesAndCollocationsScore() {
		return useOfCorrectPhrasesAndCollocationsScore;
	}

	public void setUseOfCorrectPhrasesAndCollocationsScore(int useOfCorrectPhrasesAndCollocationsScore) {
		this.useOfCorrectPhrasesAndCollocationsScore = useOfCorrectPhrasesAndCollocationsScore;
	}

	public int getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(int overallScore) {
		this.overallScore = overallScore;
	}

	public String getImprovementAreas() {
		return improvementAreas;
	}

	public void setImprovementAreas(String improvementAreas) {
		this.improvementAreas = improvementAreas;
	}
	  
	  

}
