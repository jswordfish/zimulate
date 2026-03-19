package com.v2.competency.management.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.v2.competency.management.elevanlabs.dtos.Root;
import com.v2.competency.management.service.AgentService;
@Service
public class ElevenLabsAgentService implements AgentService{
	
@Autowired	
PropertyConfig config;

private static RestTemplate restTemplate;

private final HttpClient httpClient = HttpClient.newHttpClient();

	@PostConstruct
	public void init() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(requestFactory);
	}

	@Override
	public String createOrUpdateKnowledgeBase(File file, String knowledBaseName) {
		// TODO Auto-generated method stub
		String knowledeBaseId = findKnowledgeBaseIdByName(knowledBaseName);
		if(knowledeBaseId == null) {
			knowledeBaseId = uploadKnowledgeBaseFromPdf(file.getAbsolutePath(), knowledBaseName);
		}
		else {
			updateKnowledgeBaseFromPdf(knowledeBaseId, file.getAbsolutePath(), knowledBaseName);
		}
		return knowledeBaseId;
	}

	@Override
	public String createOrUpdateAgent(String agentName, String agentPrompt, String knowledgeBaseId) {
		// TODO Auto-generated method stub
		String agentId = findAgentIdByName(agentName);
		if(agentId == null) {
			return createAgent(agentName, agentPrompt, knowledgeBaseId);
		}
		else {
			return updateAgent(agentId, agentName, agentPrompt, knowledgeBaseId);
		}
	}
	
	private String findKnowledgeBaseIdByName(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
            		config.getElevenLabsApiUrl() + "/knowledge-base",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            System.out.println("📡 KB List Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("knowledge_bases");
                if (items != null) {
                    for (Map<String, Object> kb : items) {
                        if (name.equals(kb.get("name"))) {
                            return (String) kb.get("id");
                        }
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("❌ KB List Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
        return null;
    }
	
	private String uploadKnowledgeBaseFromPdf(String filePath, String kbName) {
        File pdfFile = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(pdfFile));
        body.add("name", kbName);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                config.getElevenLabsApiUrl() + "/knowledge-base/file",
                requestEntity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            String kbId = (String) response.getBody().get("id");
            System.out.println("📄 Created Knowledge Base ID: " + kbId);
            return kbId;
        }
        throw new RuntimeException("Failed to upload knowledge base: " + response);
    }
	
	private String updateKnowledgeBaseFromPdf(String kbId, String filePath, String kbName) {
        File pdfFile = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(pdfFile));
        body.add("name", kbName);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = config.getElevenLabsApiUrl() + "/knowledge-base/" + kbId;

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("🔄 Updated Knowledge Base: " + kbId);
            return kbId;
        }
        throw new RuntimeException("Failed to update knowledge base: " + response);
    }
	
	private String createAgent(String name, String prompt, String knowledgeBaseId) {
        return sendAgentPayload(null, name, prompt, knowledgeBaseId, false);
    }
	
	private String updateAgent(String agentId, String name, String prompt, String knowledgeBaseId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> promptConfig = new HashMap<>();
        promptConfig.put("prompt", prompt);
        promptConfig.put("llm", "gpt-4o-mini");
        promptConfig.put("temperature", 0);
        promptConfig.put("max_tokens", -1);

        Map<String, Object> agentConfig = new HashMap<>();
        agentConfig.put("prompt", promptConfig);
        agentConfig.put("language", "en");

        Map<String, Object> kbEntry = new HashMap<>();
        kbEntry.put("type", "file");
        kbEntry.put("id", knowledgeBaseId);
        kbEntry.put("usage_mode", "prompt");

        List<Map<String, Object>> kbList = new ArrayList<>();
        kbList.add(kbEntry);

        Map<String, Object> conversationConfig = new HashMap<>();
        conversationConfig.put("agent", agentConfig);
        conversationConfig.put("knowledge_base", kbList);

        Map<String, Object> body = new HashMap<>();
        body.put("conversation_config", conversationConfig);
        body.put("name", name);
        body.put("tags", Collections.singletonList("api-updated"));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = config.getElevenLabsApiUrl() + "/agents/" + agentId;

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                requestEntity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("🔄 Updated Agent: " + agentId);
            return agentId;
        }
        throw new RuntimeException("Failed to update agent: " + response);
    }

    private String sendAgentPayload(String agentId, String name, String prompt, String knowledgeBaseId, boolean isUpdate) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> promptConfig = new HashMap<>();
        promptConfig.put("prompt", prompt);
        promptConfig.put("llm", "gpt-4o-mini");
        promptConfig.put("temperature", 0);
        promptConfig.put("max_tokens", -1);

        Map<String, Object> agentConfig = new HashMap<>();
        agentConfig.put("prompt", promptConfig);
        agentConfig.put("language", "en");

        Map<String, Object> kbEntry = new HashMap<>();
        kbEntry.put("type", "file");
        kbEntry.put("id", knowledgeBaseId);
        kbEntry.put("usage_mode", "prompt");

        List<Map<String, Object>> kbList = new ArrayList<>();
        kbList.add(kbEntry);

        Map<String, Object> conversationConfig = new HashMap<>();
        conversationConfig.put("agent", agentConfig);
        conversationConfig.put("knowledge_base", kbList);

        Map<String, Object> body = new HashMap<>();
        body.put("conversation_config", conversationConfig);
        body.put("name", name);
        body.put("tags", Collections.singletonList("api-created"));
        if (isUpdate) {
            body.put("agent_id", agentId);
        }

        String url = isUpdate ? config.getElevenLabsApiUrl() + "/agents/update" : config.getElevenLabsApiUrl() + "/agents/create";

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String id = isUpdate ? agentId : (String) response.getBody().get("agent_id");

            // Print extra details for debugging
            System.out.println((isUpdate ? "🔄 Updated Agent: " : "✅ Created Agent: ") + id);
            System.out.println("📂 Linked Knowledge Base ID: " + knowledgeBaseId);

            return id;
        }
        throw new RuntimeException("Failed to " + (isUpdate ? "update" : "create") + " agent: " + response);
    }
    
    private String findAgentIdByName(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getElevenLabsKey());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    config.getElevenLabsApiUrl() + "/agents",
                    HttpMethod.GET,
                    requestEntity,
                    Map.class
            );

            System.out.println("📡 Agents List Response: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> agents = (List<Map<String, Object>>) response.getBody().get("agents");
                if (agents != null) {
                    for (Map<String, Object> agent : agents) {
                        if (name.equals(agent.get("name"))) {
                            return (String) agent.get("agent_id");
                        }
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("❌ Agent List Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
        return null;
    }

	@Override
	public String createOrUpdateAgent(Root root, String agentId) {
		 HttpHeaders headers = new HttpHeaders();
	        headers.set("xi-api-key", config.getElevenLabsKey());
	        headers.setContentType(MediaType.APPLICATION_JSON);
	    String updateOrCreateUrl =  agentId == null?config.getElevenLabsApiUrl()+"/create":config.getElevenLabsApiUrl()+"/"+agentId;
	    System.out.println(updateOrCreateUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(updateOrCreateUrl))
                .header("xi-api-key", config.getElevenLabsKey())
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(root.toString(), StandardCharsets.UTF_8))
                .build();
        
        return sendRequest(request);
	}
	
	private String sendRequest(HttpRequest request) throws RuntimeException {
        HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
        if (response.statusCode() >= 400) {
            throw new RuntimeException("API Error: " + response.statusCode() + " - " + response.body());
        }
        return response.body();
    }

}
