package com.googlecloud.vertex.ai.roleplay.insights.dto.newversion;

import java.util.Objects;

public class Section{
	
	String header;
	
	String additional;
	
	String description;
	
	public Section(String header, String description) {
		super();
		this.header = header;
		this.description = description;
	}
	
	

	public Section(String header, String additional, String description) {
		super();
		this.header = header;
		this.additional = additional;
		this.description = description;
	}



	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public String getAdditional() {
			if(this.additional == null) {
				return "";
			}
		return additional;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}



	@Override
	public int hashCode() {
		return Objects.hash(additional, header);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Section other = (Section) obj;
		return Objects.equals(getAdditional(), other.getAdditional()) && Objects.equals(getHeader(), other.getHeader());
	}
	
	
}