package com.v2.liveavatar.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@lombok.Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
	private String name;

    private String prompt;

    @JsonProperty("opening_text")
    private String openingText;

    private String id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("required_dynamic_variables")
    private List<String> requiredDynamicVariables;

    private List<Link> links;
}
