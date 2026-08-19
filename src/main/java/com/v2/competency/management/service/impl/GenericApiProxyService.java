package com.v2.competency.management.service.impl;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.v2.competency.management.config.ApiKeyProperties;
import com.v2.competency.management.dtos.GenericApiRequest;
import com.v2.competency.management.entities.ApiProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericApiProxyService {

    private final RestTemplate restTemplate;
    private final ApiKeyProperties apiKeyProperties;

    public ResponseEntity<Object> callExternalApi(GenericApiRequest request) {

        ApiProvider provider = request.getProvider();

        // 1. Make sure the url actually belongs to this provider's domain.
        //    Prevents the frontend from redirecting our API key to some other host.
        validateUrlBelongsToProvider(request.getUrl(), provider);

        // 2. Resolve the HTTP method
        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod().toUpperCase());
        if (httpMethod == null) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + request.getMethod());
        }

        // 3. Get the stored API key for this provider
        String apiKey = apiKeyProperties.getKeys().get(provider.name());
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

        // 5. Build final URI, adding query params for GET (or any method that sends params)
        URI uri = buildUri(request.getUrl(), request.getParams());

        // 6. Build request entity (body will simply be ignored by RestTemplate for GET)
        HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);

        // 7. Call the external API
        try {
            log.info("Calling external API. provider={}, method={}, uri={}", provider, httpMethod, uri);
            return restTemplate.exchange(uri, httpMethod, entity, Object.class);
        } catch (HttpStatusCodeException ex) {
            // Forward the external API's actual error status + body back to frontend
            log.warn("External API call failed. provider={}, status={}, body={}",
                    provider, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
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
