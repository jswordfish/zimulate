package com.googlecloud.vertex.ai.communication.dto;

public class QuestionSpecific {
	
	private String questionId;
	  private String questionText;
	  
	  private String answer;
	  
	  Content contentAnalysis = new Content();
	  Grammar grammarAnalysis = new Grammar();
	  Interaction interactionAnalysis = new Interaction();
	  Lexical lexicalAnalysis = new Lexical() ;
	  
	  
	  
	  
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public Content getContentAnalysis() {
		return contentAnalysis;
	}
	public void setContentAnalysis(Content contentAnalysis) {
		this.contentAnalysis = contentAnalysis;
	}
	public Grammar getGrammarAnalysis() {
		return grammarAnalysis;
	}
	public void setGrammarAnalysis(Grammar grammarAnalysis) {
		this.grammarAnalysis = grammarAnalysis;
	}
	public Interaction getInteractionAnalysis() {
		return interactionAnalysis;
	}
	public void setInteractionAnalysis(Interaction interactionAnalysis) {
		this.interactionAnalysis = interactionAnalysis;
	}
	public Lexical getLexicalAnalysis() {
		return lexicalAnalysis;
	}
	public void setLexicalAnalysis(Lexical lexicalAnalysis) {
		this.lexicalAnalysis = lexicalAnalysis;
	}
	  
	  

}
