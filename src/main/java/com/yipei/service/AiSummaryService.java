package com.yipei.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yipei.config.AiProperties;
import com.yipei.entity.ServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class AiSummaryService {
    private static final Logger log = LoggerFactory.getLogger(AiSummaryService.class);
    private static final int MAX_SUMMARY_LENGTH = 800;
    private static final String SYSTEM_PROMPT = """
            You summarize medical escort service requests for an escort worker. Write concise Simplified Chinese in 1-3 sentences.
            Include the service purpose, time and location when provided, and notable mobility or communication needs.
            Do not make medical diagnoses, treatment recommendations, or include contact names and phone numbers.
            """;

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiSummaryService(AiProperties aiProperties, ObjectMapper objectMapper) {
        this.aiProperties = aiProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(Math.max(1, aiProperties.getTimeoutSeconds())))
                .build();
    }

    public Optional<String> generate(ServiceRequest serviceRequest) {
        if (!aiProperties.isConfigured()) {
            return Optional.empty();
        }

        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", aiProperties.getModel());
            body.put("temperature", 0.2);
            body.put("max_tokens", Math.max(32, aiProperties.getMaxTokens()));

            ArrayNode messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", SYSTEM_PROMPT);
            messages.addObject().put("role", "user").put("content", buildUserPrompt(serviceRequest));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(chatCompletionsUrl()))
                    .timeout(Duration.ofSeconds(Math.max(1, aiProperties.getTimeoutSeconds())))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + aiProperties.getApiKey().trim())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("AI summary request failed with HTTP status {}", response.statusCode());
                return Optional.empty();
            }

            JsonNode content = objectMapper.readTree(response.body())
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content");
            String summary = normalize(content.asText());
            return summary.isBlank() ? Optional.empty() : Optional.of(summary);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("AI summary request was interrupted");
            return Optional.empty();
        } catch (IOException | RuntimeException exception) {
            log.warn("AI summary request failed: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    private String chatCompletionsUrl() {
        String baseUrl = aiProperties.getBaseUrl().trim();
        return baseUrl.endsWith("/")
                ? baseUrl + "chat/completions"
                : baseUrl + "/chat/completions";
    }

    private String buildUserPrompt(ServiceRequest request) {
        return """
                Service type: %s
                Service time: %s
                Hospital: %s
                Department: %s
                Request details: %s
                Special notes: %s
                """.formatted(
                valueOrDash(request.getServiceType()),
                valueOrDash(request.getServiceDate()),
                valueOrDash(request.getHospitalName()),
                valueOrDash(request.getDepartment()),
                valueOrDash(request.getRequirement()),
                valueOrDash(request.getSpecialNotes())
        );
    }

    private String valueOrDash(Object value) {
        return value == null || value.toString().isBlank() ? "-" : value.toString();
    }

    private String normalize(String summary) {
        String normalized = summary == null ? "" : summary.replaceAll("\\s+", " ").trim();
        return normalized.length() > MAX_SUMMARY_LENGTH
                ? normalized.substring(0, MAX_SUMMARY_LENGTH)
                : normalized;
    }
}
