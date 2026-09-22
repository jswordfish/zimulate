package com.v2.competency.management.service.impl;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.v2.competency.management.dtos.GenericApiRequest;
import com.v2.competency.management.entities.ApiProvider;
import com.v2.competency.management.service.MiscellaneousService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericApiProxyService {

    private final RestTemplate restTemplate;
    
    @Autowired
    PropertyConfig config;
    
    @Autowired
    MiscellaneousService miscellaneousService;

    public ResponseEntity<Object> callExternalApi(GenericApiRequest request) {

        ApiProvider provider = request.getProvider();

        // 1. Make sure the url actually belongs to this provider's domain.
        validateUrlBelongsToProvider(request.getUrl(), provider);

        // 2. Resolve the HTTP method
        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod().toUpperCase());
        if (httpMethod == null) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + request.getMethod());
        }

        // 3. Get the stored API key for this provider
        String apiKey = miscellaneousService.getValue(provider.name());

        if ((apiKey == null || apiKey.isEmpty()) && provider == ApiProvider.ELEVEN_LABS) {
            log.warn("Using HARDCODED ElevenLabs API key for testing — remove this before deploying!");
        }

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("No API key configured for provider: " + provider);
        }

        // 4. Build headers: frontend headers + our injected auth header + content type
        HttpHeaders headers = new HttpHeaders();
        if (request.getHeaders() != null) {
            request.getHeaders().forEach(headers::add);
        }
        headers.set(provider.getAuthHeaderName(), apiKey);

        MediaType mediaType = (request.getContentType() != null)
                ? MediaType.parseMediaType(request.getContentType())
                : MediaType.APPLICATION_JSON;
        headers.setContentType(mediaType);

        // 5. Build final URI
        URI uri = buildUri(request.getUrl(), request.getParams());

        // 6. Build request entity
        HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);

        // Print the full request object as a String
        System.out.println("=== Full Request ===");
        System.out.println(request);

        // 7. Call the external API
        try {
            log.info("Calling external API. provider={}, method={}, uri={}", provider, httpMethod, uri);
            
            ResponseEntity<Object> response = restTemplate.exchange(uri, httpMethod, entity, Object.class);

            // Print response success indicator
            System.out.println("=== Response Generated Successfully (Status: " + response.getStatusCode() + ") ===");

            return response;
        } catch (HttpStatusCodeException ex) {
            // Print response failure indicator
            System.out.println("=== Response Failed (Status: " + ex.getStatusCode() + ") ===");

            log.warn("External API call failed. provider={}, status={}, body={}",
                    provider, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            System.out.println("=== Response Failed with Exception: " + ex.getMessage() + " ===");
            throw ex;
        }
    }

    private URI buildUri(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        return builder.build().encode().toUri();
    }

    private void validateUrlBelongsToProvider(String url, ApiProvider provider) {
        if (url == null || !url.startsWith(provider.getBaseUrl())) {
            throw new IllegalArgumentException(
                    "url must start with " + provider.getBaseUrl() + " for provider " + provider);
        }
    }
}
