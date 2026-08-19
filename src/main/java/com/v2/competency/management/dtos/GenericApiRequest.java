package com.v2.competency.management.dtos;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.v2.competency.management.entities.ApiProvider;

import lombok.Data;

@Data
public class GenericApiRequest {

    @NotBlank(message = "method is required, e.g. GET or POST")
    private String method;

    @NotBlank(message = "url is required")
    private String url;

    @NotNull(message = "provider is required, e.g. ELEVEN_LABS")
    private ApiProvider provider;

    // optional extra headers from the frontend
    private Map<String, String> headers;

    // used for POST/PUT/PATCH
    private Object body;

    // used for GET query params
    private Map<String, String> params;

    // e.g. "application/json"; defaults to application/json if not passed
    private String contentType;
}
