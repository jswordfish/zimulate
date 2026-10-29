package com.v2.competency.management.service.impl;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
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
import com.v2.competency.management.service.GeminiAudioVideoService;
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
    
    @Autowired
	GeminiAudioVideoService geminiService;
    

		public ResponseEntity<?> callExternalApi(GenericApiRequest request) {
		
		    ApiProvider provider = request.getProvider();
		
		    if (provider == null) {
		        throw new IllegalArgumentException("Provider is required");
		    }
		
		    // Gemini uses the configured Vertex AI SDK
		    if (provider == ApiProvider.GEMINI) {
		        if (request.getPrompt() == null
		                || request.getPrompt().trim().isEmpty()) {
		            throw new IllegalArgumentException(
		                    "Prompt is required for Gemini");
		        }
		
		        try {
		            String result = geminiService.generateTextResponse(
		                    request.getPrompt());
		
		            return ResponseEntity.ok()
		                    .contentType(MediaType.TEXT_PLAIN)
		                    .body(result);
		
		        } catch (Exception ex) {
		            log.error("Gemini text generation failed", ex);
		            throw new RuntimeException(
		                    "Gemini text generation failed", ex);
		        }
		    }
		
		    // Existing HTTP-provider logic remains unchanged below
		
		    if (request.getUrl() == null
		            || request.getUrl().trim().isEmpty()) {
		        throw new IllegalArgumentException("URL is required");
		    }
		
		    if (request.getMethod() == null
		            || request.getMethod().trim().isEmpty()) {
		        throw new IllegalArgumentException("Method is required");
		    }
		
		    validateUrlBelongsToProvider(request.getUrl(), provider);
		
		    HttpMethod httpMethod =
		            HttpMethod.resolve(request.getMethod().toUpperCase());
		
		    if (httpMethod == null) {
		        throw new IllegalArgumentException(
		                "Unsupported HTTP method: " + request.getMethod());
		    }
		
		    String apiKey = miscellaneousService.getValue(provider.name());
		
		    if (apiKey == null || apiKey.isEmpty()) {
		        throw new IllegalStateException(
		                "No API key configured for provider: " + provider);
		    }
		
		    HttpHeaders headers = new HttpHeaders();
		
		    if (request.getHeaders() != null) {
		        request.getHeaders().forEach(headers::add);
		    }
		
		    headers.set(provider.getAuthHeaderName(), apiKey);
		
		    MediaType mediaType = request.getContentType() != null
		            ? MediaType.parseMediaType(request.getContentType())
		            : MediaType.APPLICATION_JSON;
		
		    headers.setContentType(mediaType);
		
		    URI uri = buildUri(request.getUrl(), request.getParams());
		
		    HttpEntity<Object> entity =
		            new HttpEntity<>(request.getBody(), headers);
		
		    try {
		        log.info("Calling external API. provider={}, method={}, uri={}",
		                provider, httpMethod, uri);
		
		        ResponseEntity<byte[]> response =
		                restTemplate.exchange(uri, httpMethod, entity, byte[].class);
		
		        HttpHeaders responseHeaders = new HttpHeaders();
		
		        MediaType responseContentType =
		                response.getHeaders().getContentType();
		
		        if (responseContentType != null) {
		            responseHeaders.setContentType(responseContentType);
		        }
		
		        ContentDisposition disposition =
		                response.getHeaders().getContentDisposition();
		
		        if (disposition != null && disposition.getType() != null) {
		            responseHeaders.setContentDisposition(disposition);
		        }
		
		        long contentLength = response.getHeaders().getContentLength();
		
		        if (contentLength >= 0) {
		            responseHeaders.setContentLength(contentLength);
		        }
		
		        return new ResponseEntity<>(
		                response.getBody(),
		                responseHeaders,
		                response.getStatusCode());
		
		    } catch (HttpStatusCodeException ex) {
		        log.warn("External API failed. provider={}, status={}, body={}",
		                provider, ex.getStatusCode(), ex.getResponseBodyAsString());
		
		        return ResponseEntity.status(ex.getStatusCode())
		                .contentType(MediaType.APPLICATION_JSON)
		                .body(ex.getResponseBodyAsString());
		
		    } catch (Exception ex) {
		        log.error("External API call failed. provider={}, uri={}",
		                provider, uri, ex);
		        throw ex;
		    }
		}



//    public ResponseEntity<?> callExternalApi(GenericApiRequest request) {
//
//        ApiProvider provider = request.getProvider();
//
//        // 1. Make sure the url actually belongs to this provider's domain.
//        validateUrlBelongsToProvider(request.getUrl(), provider);
//
//        // 2. Resolve the HTTP method
//        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod().toUpperCase());
//        if (httpMethod == null) {
//            throw new IllegalArgumentException("Unsupported HTTP method: " + request.getMethod());
//        }
//
//        // 3. Get the stored API key for this provider
//        String apiKey = miscellaneousService.getValue(provider.name());
//
//        if ((apiKey == null || apiKey.isEmpty()) && provider == ApiProvider.ELEVEN_LABS) {
//            log.warn("Using HARDCODED ElevenLabs API key for testing — remove this before deploying!");
//        }
//
//        if (apiKey == null || apiKey.isEmpty()) {
//            throw new IllegalStateException("No API key configured for provider: " + provider); 
//        }
//
//        // 4. Build headers: frontend headers + our injected auth header + content type
//        HttpHeaders headers = new HttpHeaders();
//        if (request.getHeaders() != null) {
//            request.getHeaders().forEach(headers::add);
//        }
//        headers.set(provider.getAuthHeaderName(), apiKey);
//
//        MediaType mediaType = (request.getContentType() != null)
//                ? MediaType.parseMediaType(request.getContentType())
//                : MediaType.APPLICATION_JSON;
//        headers.setContentType(mediaType);
//
//        // 5. Build final URI
//        URI uri = buildUri(request.getUrl(), request.getParams());
//
//        // 6. Build request entity
//        HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);
//
//        // Print the full request object as a String
//        System.out.println("=== Full Request ===");
//        System.out.println(request);
//
//        // 7. Call the external API
//        try {
//            log.info("Calling external API. provider={}, method={}, uri={}",
//                    provider, httpMethod, uri);
//
//            // Read the response as raw bytes to support audio, images,
//            // JSON, and other response content types.
//            ResponseEntity<byte[]> response =
//                    restTemplate.exchange(uri, httpMethod, entity, byte[].class);
//
//            log.info("External API call successful. provider={}, status={}, contentType={}",
//                    provider, response.getStatusCode(),
//                    response.getHeaders().getContentType());
//
//            HttpHeaders responseHeaders = new HttpHeaders();
//
//            // Preserve the original content type.
//            MediaType contentType = response.getHeaders().getContentType();
//            if (contentType != null) {
//                responseHeaders.setContentType(contentType);
//            }
//
//            // Preserve the download filename or other disposition information.
//            ContentDisposition disposition =
//                    response.getHeaders().getContentDisposition();
//
//            if (disposition != null && disposition.getType() != null) {
//                responseHeaders.setContentDisposition(disposition);
//            }
//
//            // Preserve the response length when available.
//            long contentLength = response.getHeaders().getContentLength();
//            if (contentLength >= 0) {
//                responseHeaders.setContentLength(contentLength);
//            }
//
//            return new ResponseEntity<>(
//                    response.getBody(),
//                    responseHeaders,
//                    response.getStatusCode()
//            );
//
//        } catch (HttpStatusCodeException ex) {
//            log.warn("External API call failed. provider={}, status={}, body={}",
//                    provider, ex.getStatusCode(), ex.getResponseBodyAsString());
//
//            return ResponseEntity.status(ex.getStatusCode())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(ex.getResponseBodyAsString());
//
//        } catch (Exception ex) {
//            log.error("External API call failed. provider={}, uri={}",
//                    provider, uri, ex);
//            throw ex;
//        }
//    }

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
