package com.googlecloud.heygen.endsuer.customer.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public class UserAnalysis {
    @JsonProperty("personal_details")
    private PersonalDetails personalDetails;

    @JsonProperty("financial_profile")
    private FinancialProfile financialProfile;

    @JsonProperty("granular_intent_analysis")
    private List<GranularIntentAnalysis> granularIntentAnalysis;

    @JsonProperty("actionable_next_steps")
    private ActionableNextSteps actionableNextSteps;

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public FinancialProfile getFinancialProfile() {
        return financialProfile;
    }

    public void setFinancialProfile(FinancialProfile financialProfile) {
        this.financialProfile = financialProfile;
    }

    public List<GranularIntentAnalysis> getGranularIntentAnalysis() {
        return granularIntentAnalysis;
    }

    public void setGranularIntentAnalysis(List<GranularIntentAnalysis> granularIntentAnalysis) {
        this.granularIntentAnalysis = granularIntentAnalysis;
    }

    public ActionableNextSteps getActionableNextSteps() {
        return actionableNextSteps;
    }

    public void setActionableNextSteps(ActionableNextSteps actionableNextSteps) {
        this.actionableNextSteps = actionableNextSteps;
    }
}