package com.diamondanalytics.backend.service.impl;

import com.diamondanalytics.backend.client.GeminiApiClient;
import com.diamondanalytics.backend.dto.prediction.GamePredictionDto;
import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.model.Odds;
import com.diamondanalytics.backend.model.Prediction;
import com.diamondanalytics.backend.repository.GameRepository;
import com.diamondanalytics.backend.repository.OddsRepository;
import com.diamondanalytics.backend.repository.PredictionRepository;
import com.diamondanalytics.backend.service.PredictionService;
import com.diamondanalytics.backend.utils.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PredictionServiceImpl implements PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionServiceImpl.class);

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private OddsRepository oddsRepository;

    @Autowired
    private GeminiApiClient geminiApiClient;

    @Autowired
    private PromptBuilder promptBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public GamePredictionDto getPredictionForGame(Game game) {
        // Buscar si ya existe una predicción para este juego
        Prediction existing = predictionRepository.findByGame(game).orElse(null);
        if (existing != null) {
            return mapToDto(existing);
        }

        // Si no existe, generarla
        Prediction newPrediction = generatePrediction(game);
        if (newPrediction != null) {
            predictionRepository.save(newPrediction);
            return mapToDto(newPrediction);
        }
        return null;
    }

    @Override
    @Transactional
    public void generatePredictionsForTodaysGames() {
        ZoneId chicago = ZoneId.of("America/Chicago");
        ZonedDateTime startOfDay = ZonedDateTime.now(chicago).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Instant startUtc = startOfDay.toInstant();
        Instant endUtc = endOfDay.toInstant();

        List<Game> games = gameRepository.findByStartTimeBetween(startUtc, endUtc);
        log.info("Generando predicciones para {} juegos de hoy", games.size());

        for (Game game : games) {
            if (!predictionRepository.existsByGame(game)) {
                Prediction prediction = generatePrediction(game);
                if (prediction != null) {
                    predictionRepository.save(prediction);
                }
            }
        }
    }

    private Prediction generatePrediction(Game game) {
        try {
            String prompt = promptBuilder.buildPredictionPrompt(game);
            String jsonResponse = geminiApiClient.generateContent(prompt);

            // Limpiar posible markdown
            jsonResponse = jsonResponse.trim();
            if (jsonResponse.startsWith("```json")) {
                jsonResponse = jsonResponse.substring(7);
            }
            if (jsonResponse.endsWith("```")) {
                jsonResponse = jsonResponse.substring(0, jsonResponse.length() - 3);
            }
            jsonResponse = jsonResponse.trim();

            JsonNode root = objectMapper.readTree(jsonResponse);

            Prediction pred = new Prediction();
            pred.setGame(game);
            pred.setHomeWinProbability(root.path("homeWinProbability").asDouble());
            pred.setAwayWinProbability(root.path("awayWinProbability").asDouble());
            pred.setRecommendedBet(root.path("recommendedBet").asText());
            pred.setConfidenceScore(root.path("confidenceScore").asDouble());
            pred.setAnalysis(root.path("analysis").asText());

            // Calcular EV si hay cuotas
            List<Odds> oddsList = oddsRepository.findByGameOrderByTimestampDesc(game);
            if (!oddsList.isEmpty()) {
                Odds latest = oddsList.get(0);
                if ("HOME".equals(pred.getRecommendedBet()) && latest.getHomeMoneyline() != null) {
                    double prob = pred.getHomeWinProbability();
                    double odds = latest.getHomeMoneyline();
                    pred.setExpectedValue((prob * odds) - 1.0);
                } else if ("AWAY".equals(pred.getRecommendedBet()) && latest.getAwayMoneyline() != null) {
                    double prob = pred.getAwayWinProbability();
                    double odds = latest.getAwayMoneyline();
                    pred.setExpectedValue((prob * odds) - 1.0);
                }
            }

            return pred;
        } catch (Exception e) {
            log.error("Error generando predicción para juego {}", game.getId(), e);
            return null;
        }
    }

    private GamePredictionDto mapToDto(Prediction pred) {
        GamePredictionDto dto = new GamePredictionDto();
        dto.setGameId(pred.getGame().getId());
        dto.setHomeTeam(pred.getGame().getHomeTeam().getName());
        dto.setAwayTeam(pred.getGame().getAwayTeam().getName());
        dto.setStartTime(pred.getGame().getStartTime().toString());
        dto.setHomeWinProbability(pred.getHomeWinProbability());
        dto.setAwayWinProbability(pred.getAwayWinProbability());
        dto.setRecommendedBet(pred.getRecommendedBet());
        dto.setConfidenceScore(pred.getConfidenceScore());
        dto.setAnalysis(pred.getAnalysis());
        dto.setExpectedValue(pred.getExpectedValue());
        return dto;
    }
}