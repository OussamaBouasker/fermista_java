package tn.fermista.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class GeminiService {
    private static final String API_KEY = "AIzaSyBWpRselqhaj010LKzVjpJqFNR2u9D_XWQ";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=" + API_KEY;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String generateDescription(String keywords) throws Exception {
        if (keywords == null || keywords.trim().isEmpty()) {
            throw new IllegalArgumentException("Les mots-clés ne peuvent pas être vides");
        }

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        
        // Amélioration du prompt pour de meilleures descriptions
        String prompt = "Générer une description détaillée et professionnelle pour un workshop ou une formation sur le thème suivant: " + keywords + 
                       ". La description doit être engageante, informative et inclure les points clés du sujet.";
        
        part.put("text", prompt);
        
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Erreur API: " + response.statusCode() + " - " + response.body());
        }

        try {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode candidatesNode = rootNode.path("candidates").get(0);
            JsonNode contentNode = candidatesNode.path("content");
            JsonNode partsNode = contentNode.path("parts").get(0);
            String text = partsNode.path("text").asText();
            
            if (text == null || text.isEmpty()) {
                throw new Exception("La réponse de l'API ne contient pas de texte");
            }
            
            return text;
        } catch (Exception e) {
            throw new Exception("Erreur lors du traitement de la réponse: " + e.getMessage());
        }
    }
} 