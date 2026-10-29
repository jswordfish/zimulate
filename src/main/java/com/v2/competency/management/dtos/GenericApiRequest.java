package com.v2.competency.management.dtos;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.v2.competency.management.entities.ApiProvider;

import lombok.Data;


@Data
public class GenericApiRequest {

    private String method;

    private String url;

    @NotNull(message = "provider is required")
    private ApiProvider provider;

    private Map<String, String> headers;

    private Object body;

    private Map<String, String> params;

    private String contentType;

    private String prompt;
    
}

