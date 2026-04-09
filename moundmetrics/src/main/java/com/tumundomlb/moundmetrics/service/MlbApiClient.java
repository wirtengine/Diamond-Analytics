package com.tumundomlb.moundmetrics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tumundomlb.moundmetrics.config.MlbApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlbApiClient {

    private final RestTemplate restTemplate;
    private final MlbApiProperties properties;

    public JsonNode getTeams() {
        String url = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path("/teams")
                .queryParam("sportId", 1)
                .toUriString();
        log.info("Fetching teams from {}", url);
        return restTemplate.getForObject(url, JsonNode.class);
    }

    public JsonNode getSchedule(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // El schedule sigue usando la versión v1
        String url = UriComponentsBuilder.fromHttpUrl("https://statsapi.mlb.com/api/v1")
                .path("/schedule")
                .queryParam("sportId", 1)
                .queryParam("date", dateStr)
                .queryParam("hydrate", "team,linescore,probablePitcher")
                .toUriString();
        log.info("Fetching schedule for {} from {}", dateStr, url);
        return restTemplate.getForObject(url, JsonNode.class);
    }

    public JsonNode getLiveGame(long gamePk) {
        // El feed en vivo requiere v1.1
        String url = UriComponentsBuilder.fromHttpUrl("https://statsapi.mlb.com/api/v1.1")
                .path("/game/{gamePk}/feed/live")
                .buildAndExpand(gamePk)
                .toUriString();
        log.info("Fetching live game data for gamePk {}", gamePk);
        try {
            return restTemplate.getForObject(url, JsonNode.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Live game feed not found for gamePk {} (404). Skipping.", gamePk);
            return null;
        } catch (Exception e) {
            log.error("Error fetching live game for {}: {}", gamePk, e.getMessage());
            return null;
        }
    }
}