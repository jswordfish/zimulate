package com.googlecloud.heygen.endsuer.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionableNextSteps {
	@JsonProperty("human_escalation_required")
    private Boolean humanEscalationRequired;

    @JsonProperty("escalation_reason")
    private String escalationReason;

    @JsonProperty("recommended_sales_follow_up")
    private String recommendedSalesFollowUp;

    public Boolean getHumanEscalationRequired() {
        return humanEscalationRequired;
    }

    public void setHumanEscalationRequired(Boolean humanEscalationRequired) {
        this.humanEscalationRequired = humanEscalationRequired;
    }

    public String getEscalationReason() {
        return escalationReason;
    }

    public void setEscalationReason(String escalationReason) {
        this.escalationReason = escalationReason;
    }

    public String getRecommendedSalesFollowUp() {
        return recommendedSalesFollowUp;
    }

    public void setRecommendedSalesFollowUp(String recommendedSalesFollowUp) {
        this.recommendedSalesFollowUp = recommendedSalesFollowUp;
    }
}
