package com.googlecloud.heygen.endsuer.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GranularIntentAnalysis {
	@JsonProperty("sales_intent")
    private SalesIntent salesIntent;

    @JsonProperty("behavioral_analysis")
    private BehavioralAnalysis behavioralAnalysis;

    public SalesIntent getSalesIntent() {
        return salesIntent;
    }

    public void setSalesIntent(SalesIntent salesIntent) {
        this.salesIntent = salesIntent;
    }

    public BehavioralAnalysis getBehavioralAnalysis() {
        return behavioralAnalysis;
    }

    public void setBehavioralAnalysis(BehavioralAnalysis behavioralAnalysis) {
        this.behavioralAnalysis = behavioralAnalysis;
    }
}
