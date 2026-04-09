package com.diamondanalytics.backend.client;

import com.diamondanalytics.backend.config.GeminiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class GeminiApiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiClient.class);

    private final WebClient webClient;
    private final GeminiConfig geminiConfig;
    private final ObjectMapper objectMapper;

    public GeminiApiClient(@Qualifier("geminiWebClient") WebClient webClient,
                           GeminiConfig geminiConfig,
                           ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.geminiConfig = geminiConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * Envía un prompt y obtiene la respuesta en texto.
     */
    public String generateContent(String prompt) {
        try {
            ObjectNode requestBody = buildRequestBody(prompt);
            log.debug("Enviando prompt a Gemini: {}", prompt);

            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("key", geminiConfig.getApiKey())
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .maxBackoff(Duration.ofSeconds(10)))
                    .block();

            return extractTextFromResponse(response);
        } catch (Exception e) {
            log.error("Error al llamar a Gemini API", e);
            return "Error al generar predicción: " + e.getMessage();
        }
    }

    private ObjectNode buildRequestBody(String prompt) {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");

        ObjectNode content = contents.addObject();
        content.put("role", "user");
        ArrayNode parts = content.putArray("parts");
        parts.addObject().put("text", prompt);

        // Configuración de generación
        ObjectNode generationConfig = root.putObject("generationConfig");
        generationConfig.put("temperature", 0.2);  // más determinista
        generationConfig.put("maxOutputTokens", 1024);
        generationConfig.put("topP", 0.8);
        generationConfig.put("topK", 40);

        return root;
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            // Si no hay texto, podría ser un error de seguridad o formato
            log.warn("Respuesta de Gemini sin texto: {}", jsonResponse);
            return "No se pudo generar una predicción en este momento.";
        } catch (Exception e) {
            log.error("Error parseando respuesta de Gemini", e);
            return "Error al procesar la respuesta del modelo.";
        }
    }
}