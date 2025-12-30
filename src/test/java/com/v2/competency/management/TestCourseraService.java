package com.v2.competency.management;

import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TestCourseraService {
	
	// 1. CONSTANTS
    private static final String AUTH_URL = "https://api.coursera.com/oauth2/client_credentials/token";
    private static final String SEARCH_URL = "https://api.coursera.org/api/courses.v1";
    
    // REPLACE THESE WITH YOUR ACTUAL KEYS
    private static final String CLIENT_KEY = "YzJfMUJhLMuhXESPNf0jElvuPjjFwNSGAnXSR0Alk2BzCEbz";
    private static final String CLIENT_SECRET = "oymdYwBbRjG6otz7fvGOLYpDi8SIv2HqEIFEfUiwzfiPAnFRiAJ0NBQdkwmAfmua";

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    
    /**
     * Authenticates using OAuth2 Client Credentials flow.
     */
    private static String getAccessToken(String key, String secret) throws Exception {
        // Create the Basic Auth header (Base64 encoded key:secret)
        String authString = key + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        // Form data for the POST request
        String formData = "grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get token: " + response.body());
        }

        JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
        return jsonObject.get("access_token").getAsString();
    }

    /**
     * Searches the catalog and prints Course Names and Links.
     */
    private static void searchCourses(String token, String query) throws Exception {
        // Encode the query parameter
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // We specifically request 'slug' and 'name' fields.
        // The 'slug' is required to build the public URL.
        String url = String.format("%s?q=search&query=%s&fields=slug,name,description,primaryLanguages", 
                                   SEARCH_URL, encodedQuery);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Search failed: " + response.body());
        }

        // Parse and Display Results
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        if (jsonResponse.has("elements")) {
            JsonArray courses = jsonResponse.getAsJsonArray("elements");
            
            System.out.println("\n--- Search Results for: " + query + " ---");
            for (JsonElement element : courses) {
                JsonObject course = element.getAsJsonObject();
                
                String name = course.has("name") ? course.get("name").getAsString() : "Unknown";
                String slug = course.has("slug") ? course.get("slug").getAsString() : "";
                
                // Construct the public link
                String courseLink = "https://www.coursera.org/learn/" + slug;

                System.out.printf("Course: %s%nLink:   %s%n%n", name, courseLink);
            }
        } else {
            System.out.println("No courses found.");
        }
    }
	
	
	@Test
	public void testCourseraApis() throws Exception {
		String token = getAccessToken(CLIENT_KEY, CLIENT_SECRET);
		searchCourses(token, "Empathy");
	}

}
