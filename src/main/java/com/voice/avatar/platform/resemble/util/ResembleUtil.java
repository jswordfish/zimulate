package com.voice.avatar.platform.resemble.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voice.avatar.platform.resemble.dto.Response;

public class ResembleUtil {
	static ObjectMapper mapper = new ObjectMapper();
	
	
	
	public static Response fetchAudio(String text) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			String requestBody = prepareRequest(text);
			HttpRequest request = HttpRequest
				     .newBuilder()
				     .uri(URI.create("https://app.resemble.ai/api/v2/projects/3a682a15/clips"))
				     .POST(HttpRequest.BodyPublishers.ofString(requestBody))
				     .header("Accept", "application/json")
				     .header("Content-Type", "application/json")
				     .header("Authorization", "Token token=1ZQQ9qRpNudfO2zt4GOtZgtt")
				     .build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String data = response.body();
			Response r = mapper.readValue(data.getBytes(), Response.class);
			return r;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
	}
	
	private static String prepareRequest(String text) {
		  try {
			Map<String, Object> values = new HashMap<String, Object>() {
			   {
			    put("body", text);
			    put("voice_uuid", "33e64cd2");
			    put("is_public", true);
			    put("is_archived",false);
			   }
			  };

			  ObjectMapper objectMapper = new ObjectMapper();
			  String requestBody = objectMapper.writeValueAsString(values);
			  return requestBody;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		 }

}
