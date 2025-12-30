package com.v2.competency.management.dtos;

import com.v2.competency.management.entities.User;

public class UserWithTestDto {
	
	private String testName;
    private User user;

    public UserWithTestDto(String testName, User user) {
        this.testName = testName;
        this.user = user;
    }

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    

}
