package com.v2.competency.management.webservices;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.v2.competency.management.dtos.LiveCountResponse;
import com.v2.competency.management.dtos.LiveCountStatusResponse;
import com.v2.competency.management.service.impl.PropertyConfig;

@RestController
@CrossOrigin
@RequestMapping("/api-keys")
public class MiscellaneousWebService {
	
	@Autowired
	PropertyConfig config;
	
	@Autowired
	private RestTemplate restTemplate;
	
	

	
	@GetMapping
	public String getElevenLabsApiKey(@RequestParam String token) {
		
		String key = config.getElevenLabsKey();
		
		return key;
		
	}
	
	@GetMapping("/live-count")
	public ResponseEntity<LiveCountStatusResponse> isLiveCountWithinLimit(
	        @RequestParam String token,
	        @RequestParam(required = false) String agentId) {

	    String key = config.getElevenLabsKey();

	    UriComponentsBuilder builder = UriComponentsBuilder
	            .fromHttpUrl("https://api.elevenlabs.io/v1/convai/analytics/live-count");

	    if (agentId != null && !agentId.isEmpty()) {
	        builder.queryParam("agent_id", agentId);
	    }

	    String url = builder.toUriString();

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("xi-api-key", key);

	    HttpEntity<Void> entity = new HttpEntity<>(headers);

	    ResponseEntity<LiveCountResponse> response = restTemplate.exchange(
	            url, HttpMethod.GET, entity, LiveCountResponse.class);

	    LiveCountResponse body = response.getBody();
	    int count = (body != null) ? body.getCount() : 0;

	    boolean withinLimit = count <= config.getLiveCountLimit();

	    return ResponseEntity.ok(new LiveCountStatusResponse(withinLimit, count));
	}
	
	

}
