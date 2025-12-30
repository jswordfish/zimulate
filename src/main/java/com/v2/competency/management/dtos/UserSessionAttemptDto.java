package com.v2.competency.management.dtos;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionAttemptDto {
	
	String email;
	
	String firstName;
	
	String lastName;
	
	Integer attempt;
	
	String testName;
	
	String testIdentifier;

	@Override
	public int hashCode() {
		return Objects.hash(attempt, email, testIdentifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSessionAttemptDto other = (UserSessionAttemptDto) obj;
		return Objects.equals(attempt, other.attempt) && Objects.equals(email, other.email)
				&& Objects.equals(testIdentifier, other.testIdentifier);
	}
	
	

}
