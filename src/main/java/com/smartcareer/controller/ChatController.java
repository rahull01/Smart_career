package com.smartcareer.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final String apiKey;

    public ChatController() {
        // Environment variable se API key load karo
        this.apiKey = System.getenv("OPENAI_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("ERROR: OPENAI_API_KEY environment variable is not set or empty!");
            // Optionally, throw RuntimeException to stop app if key is critical
            // throw new RuntimeException("OPENAI_API_KEY not set");
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        if (userMessage == null || userMessage.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
        }

        String response = askOpenAI(userMessage);
        Map<String, String> result = new HashMap<>();
        result.put("reply", response);
        return ResponseEntity.ok(result);
    }

    private String askOpenAI(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "API Key not configured.";
        }

        try {
            String body = String.format("""
                {
                  "model": "gpt-4o-mini",
                  "messages": [{"role": "user", "content": "%s"}]
                }
                """, prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("OpenAI API status: " + response.statusCode());
            System.out.println("OpenAI API response: " + response.body());

            if (response.statusCode() != 200) {
                return "OpenAI API error: " + response.body();
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray choices = json.getJSONArray("choices");
            return choices.getJSONObject(0).getJSONObject("message").getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, something went wrong.";
        }
    }
}
