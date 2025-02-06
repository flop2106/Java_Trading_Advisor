package com.project1.trading_automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.*;

@Component
public class NewsAPIHandler {
    private static final String apiKey = ConfigLoader.getProperty("newsapi.key");
    private static final Logger logger = LoggerFactory.getLogger(NewsAPIHandler.class);
    private static final String BASE_URL = "https://newsapi.org/v2/everything";

    public List<Map<String, String>> getNews(String query, int maxResults) {
        List<Map<String, String>> newsList = new ArrayList<>();
        try {
            // Build the API URL
            String apiUrl = BASE_URL + "?q=" + query + "&pageSize=" + maxResults + "&sortBy=publishedAt&apiKey=" + apiKey;
            logger.info("Request URL: " + apiUrl);
    
            // Create HTTP Client and Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();
    
            // Send Request and Get Response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("API Response: " + response.body());
    
            // Parse the Response JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode articles = rootNode.get("articles");
    
            // Check if `articles` exists and is an array
            if (articles == null || !articles.isArray()) {
                logger.warn("No articles found in response.");
                return newsList;
            }
    
            // Extract News Articles
            for (JsonNode article : articles) {
                Map<String, String> newsItem = new HashMap<>();
                newsItem.put("title", article.get("title").asText());
                newsItem.put("url", article.get("url").asText());
                newsItem.put("content", article.has("content") && !article.get("content").isNull()
                        ? article.get("content").asText()
                        : "No Content Available");
                newsList.add(newsItem);
            }
        } catch (Exception e) {
            logger.warn("Error fetching news: " + e.getMessage());
        }
        return newsList;
    }
    
    
    public static String summarizeNews(String content){
        return content.substring (0, Math.min(100, content.length())) + "...";
    }



    
}
