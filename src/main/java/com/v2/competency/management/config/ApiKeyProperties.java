package com.v2.competency.management.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "external-api")
public class ApiKeyProperties {

    private Map<String, String> keys = new HashMap<>();
    
}
