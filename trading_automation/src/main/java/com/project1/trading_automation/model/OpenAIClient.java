package com.project1.trading_automation.model;
import com.project1.trading_automation.utils.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class OpenAIClient {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = ConfigLoader.getProperty("openai.key");
    public static String sendMessageToOpenAI(String message) {
        try {
            // Create HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // Create JSON Request Body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini"); // Use "gpt-3.5-turbo" if needed
            requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", "You are a data analyst experts in predicting the next move for ticker given by the user. Do your own TA if necessary"),
                Map.of("role", "user", "content", message)
            });
            requestBody.put("temperature", 0.7);

            // Convert Map to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            // Build HTTP Request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();
            System.out.println(request);
            // Send Request and Get Response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON Response
            JsonNode rootNode = objectMapper.readTree(response.body());
            return rootNode.get("choices").get(0).get("message").get("content").asText();

        } catch (Exception e) {
            return "Error communicating with OpenAI: " + e.getMessage();
        }
    }


}
