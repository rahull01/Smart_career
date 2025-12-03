package com.smartcareer.api;

import java.util.HashMap;
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
@RequestMapping("/api/quiz")
public class QuizController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public QuizController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateQuiz(@RequestBody Map<String, String> payload) {
        try {
            String topic = payload.getOrDefault("topic", "DSA");
            String difficulty = payload.getOrDefault("difficulty", "easy");

            String url = "https://api.openai.com/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content",
                    "Generate 10 " + difficulty + " level DSA MCQ questions on topic: " + topic +
                    ". Return ONLY valid JSON in this format:\n\n" +
                    "{\n" +
                    "  \"Q1\": {\"question\": \"...\", \"options\": [\"Queue\", \"Stack\", \"Array\", \"Linked List\"], \"answer\": \"Stack\"},\n" +
                    "  \"Q2\": {\"question\": \"...\", \"options\": [\"...\"], \"answer\": \"...\"}\n" +
                    "}\n\n" +
                    "Rules:\n" +
                    "- answer MUST be EXACT STRING present in options\n" +
                    "- No explanation\n" +
                    "- No markdown\n" +
                    "- JSON must be strictly valid."
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(message));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            Map<String, Object> quiz = objectMapper.readValue(content, Map.class);

            return ResponseEntity.ok(quiz);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
