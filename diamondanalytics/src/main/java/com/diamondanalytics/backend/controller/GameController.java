package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    // ✅ Juegos de hoy en hora Nicaragua
    @GetMapping("/today")
    @PreAuthorize("isAuthenticated()")
    public List<Game> getTodaysGames() {

        ZoneId nicaraguaZone = ZoneId.of("America/Managua");

        ZonedDateTime startOfDay = LocalDate.now(nicaraguaZone).atStartOfDay(nicaraguaZone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Instant startUtc = startOfDay.toInstant();
        Instant endUtc = endOfDay.toInstant();

        return gameRepository.findByStartTimeBetween(startUtc, endUtc);
    }

    // ✅ Juegos por fecha específica (Nicaragua)
    @GetMapping("/date")
    @PreAuthorize("isAuthenticated()")
    public List<Game> getGamesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ZoneId nicaraguaZone = ZoneId.of("America/Managua");

        ZonedDateTime startOfDay = date.atStartOfDay(nicaraguaZone);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        Instant startUtc = startOfDay.toInstant();
        Instant endUtc = endOfDay.toInstant();

        return gameRepository.findByStartTimeBetween(startUtc, endUtc);
    }
}