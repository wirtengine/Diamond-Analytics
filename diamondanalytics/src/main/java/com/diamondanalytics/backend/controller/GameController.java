package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/today")
    @PreAuthorize("isAuthenticated()")
    public List<Game> getTodaysGames() {
        // Usar la zona horaria de Chicago (Central Time)
        ZoneId chicagoZone = ZoneId.of("America/Chicago");
        ZonedDateTime nowInChicago = ZonedDateTime.now(chicagoZone);
        LocalDate todayInChicago = nowInChicago.toLocalDate();

        // Convertir inicio y fin del día en Chicago a LocalDateTime (sin zona, pero representando ese instante)
        LocalDateTime start = todayInChicago.atStartOfDay(chicagoZone).toLocalDateTime();
        LocalDateTime end = todayInChicago.atTime(LocalTime.MAX).atZone(chicagoZone).toLocalDateTime();

        return gameRepository.findGamesInRange(start, end);
    }

    @GetMapping("/date")
    @PreAuthorize("isAuthenticated()")
    public List<Game> getGamesByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ZoneId chicagoZone = ZoneId.of("America/Chicago");
        LocalDateTime start = date.atStartOfDay(chicagoZone).toLocalDateTime();
        LocalDateTime end = date.atTime(LocalTime.MAX).atZone(chicagoZone).toLocalDateTime();
        return gameRepository.findGamesInRange(start, end);
    }
}