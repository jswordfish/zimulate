package com.v2.competency.management.entities;

import lombok.Getter;

@Getter
public enum ApiProvider {

    ELEVEN_LABS("https://api.elevenlabs.io", "xi-api-key"),
    HEY_GEN("https://api.heygen.com", "X-Api-Key");

    private final String baseUrl;
    private final String authHeaderName;

    ApiProvider(String baseUrl, String authHeaderName) {
        this.baseUrl = baseUrl;
        this.authHeaderName = authHeaderName;
    }
}