package com.smartcareer.api;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chatgpt")
public class GenerateQuestionsController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public GenerateQuestionsController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<Map<String, String>> generateQuestions(@RequestBody Map<String, String> payload) {
        try {
            String topic = payload.getOrDefault("topic", "DSA");
            String difficulty = payload.getOrDefault("difficulty", "easy");

            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); // replace with your real key

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", "Generate 10 " + difficulty + " level DSA questions on topic: " + topic + ". Number them like 1., 2., 3. etc. Return only the questions, no explanation.");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(message));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Split & clean
            String[] lines = content.split("\\n");
            Map<String, String> questionMap = new LinkedHashMap<>();
            int qNo = 1;

            for (String line : lines) {
                if (line.trim().matches("^\\d+\\.\\s+.*")) {
                    String question = line.trim().replaceFirst("^\\d+\\.\\s*", "");
                    questionMap.put("Q" + qNo, question);
                    qNo++;
                }
                if (qNo > 10) break; // limit to 10 questions max
            }

            return ResponseEntity.ok(questionMap);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", e.getMessage()));
        }
    }
}