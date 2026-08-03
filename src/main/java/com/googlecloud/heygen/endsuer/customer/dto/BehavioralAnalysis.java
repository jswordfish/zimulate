package com.googlecloud.heygen.endsuer.customer.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BehavioralAnalysis {
	@JsonProperty("enthusiasm_level")
    private String enthusiasmLevel;

    @JsonProperty("enthusiasm_rationale")
    private String enthusiasmRationale;

    @JsonProperty("primary_language_used")
    private String primaryLanguageUsed;

    @JsonProperty("objections_or_misconceptions")
    private List<String> objectionsOrMisconceptions;

    public String getEnthusiasmLevel() {
        return enthusiasmLevel;
    }

    public void setEnthusiasmLevel(String enthusiasmLevel) {
        this.enthusiasmLevel = enthusiasmLevel;
    }

    public String getEnthusiasmRationale() {
        return enthusiasmRationale;
    }

    public void setEnthusiasmRationale(String enthusiasmRationale) {
        this.enthusiasmRationale = enthusiasmRationale;
    }

    public String getPrimaryLanguageUsed() {
        return primaryLanguageUsed;
    }

    public void setPrimaryLanguageUsed(String primaryLanguageUsed) {
        this.primaryLanguageUsed = primaryLanguageUsed;
    }

    public List<String> getObjectionsOrMisconceptions() {
        return objectionsOrMisconceptions;
    }

    public void setObjectionsOrMisconceptions(List<String> objectionsOrMisconceptions) {
        this.objectionsOrMisconceptions = objectionsOrMisconceptions;
    }
}
