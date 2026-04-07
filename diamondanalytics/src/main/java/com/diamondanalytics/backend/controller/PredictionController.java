package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.dto.prediction.GamePredictionDto;
import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.repository.GameRepository;
import com.diamondanalytics.backend.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/game/{gameId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GamePredictionDto> getPredictionByGameId(@PathVariable Long gameId) {
        return gameRepository.findById(gameId)
                .map(game -> ResponseEntity.ok(predictionService.getPredictionForGame(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/today")
    @PreAuthorize("isAuthenticated()")
    public List<GamePredictionDto> getTodaysPredictions() {
        // Zona horaria de Nicaragua (o la que prefieras)
        ZoneId zone = ZoneId.of("America/Managua");
        ZonedDateTime startOfDay = ZonedDateTime.now(zone).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Instant startUtc = startOfDay.toInstant();
        Instant endUtc = endOfDay.toInstant();

        List<Game> games = gameRepository.findByStartTimeBetween(startUtc, endUtc);
        return games.stream()
                .map(game -> predictionService.getPredictionForGame(game))
                .collect(Collectors.toList());
    }

    @PostMapping("/generate/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> triggerPredictionGeneration() {
        predictionService.generatePredictionsForTodaysGames();
        return ResponseEntity.ok("Generación de predicciones iniciada");
    }
}