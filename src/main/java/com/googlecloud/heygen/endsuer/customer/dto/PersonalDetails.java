package com.googlecloud.heygen.endsuer.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonalDetails {
	private String name;

    @JsonProperty("contact_info")
    private String contactInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
