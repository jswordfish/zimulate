package com.googlecloud.heygen.endsuer.customer.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesIntent {
	@JsonProperty("area_of_interest")
    private String areaOfInterest;

    @JsonProperty("priority_of_interest")
    private String priorityOfInterest;

    @JsonProperty("document_readiness_status")
    private List<String> documentReadinessStatus;

    public String getAreaOfInterest() {
        return areaOfInterest;
    }

    public void setAreaOfInterest(String areaOfInterest) {
        this.areaOfInterest = areaOfInterest;
    }

    public String getPriorityOfInterest() {
        return priorityOfInterest;
    }

    public void setPriorityOfInterest(String priorityOfInterest) {
        this.priorityOfInterest = priorityOfInterest;
    }

    public List<String> getDocumentReadinessStatus() {
        return documentReadinessStatus;
    }

    public void setDocumentReadinessStatus(List<String> documentReadinessStatus) {
        this.documentReadinessStatus = documentReadinessStatus;
    }
}
