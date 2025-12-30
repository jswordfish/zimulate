package com.v2.competency.management.entities;

public enum Question_Source {
	
	AI("AI"), QUESTION_BANK_RANDOM("QUESTION_BANK_RANDOM"), QUESTION_BANK_FIXED("QUESTION_BANK_FIXED");
		
		String source;
		
		private Question_Source(String source) {
			this.source = source;
		}

		public String getSource() {
			return source;
		}

		
		
}
