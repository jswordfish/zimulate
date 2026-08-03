package com.googlecloud.heygen.endsuer.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {
	@JsonProperty("user_analysis")
    private UserAnalysis userAnalysis;

    public UserAnalysis getUserAnalysis() {
        return userAnalysis;
    }

    public void setUserAnalysis(UserAnalysis userAnalysis) {
        this.userAnalysis = userAnalysis;
    }
}
