package com.tumundomlb.moundmetrics.controller;

import com.tumundomlb.moundmetrics.service.GameSyncService;
import com.tumundomlb.moundmetrics.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TeamService teamService;
    private final GameSyncService gameSyncService;

    /**
     * Sincroniza todos los equipos de MLB en la base de datos.
     */
    @PostMapping("/sync/teams")
    public ResponseEntity<String> syncTeams() {
        teamService.syncTeams();
        return ResponseEntity.ok("Sincronización de equipos completada.");
    }

    /**
     * Sincroniza los juegos de una fecha específica.
     * Ejemplo: POST /api/admin/sync/games/2026-04-01
     */
    @PostMapping("/sync/games/{date}")
    public ResponseEntity<String> syncGames(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        gameSyncService.syncGamesForDate(date);
        return ResponseEntity.ok("Sincronización de juegos iniciada para " + date);
    }

    /**
     * Sincroniza los juegos en un rango de fechas (inclusive).
     * Ejemplo: POST /api/admin/sync/games/range?start=2026-03-26&end=2026-04-06
     */
    @PostMapping("/sync/games/range")
    public ResponseEntity<String> syncGamesRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        LocalDate current = start;
        while (!current.isAfter(end)) {
            gameSyncService.syncGamesForDate(current);
            current = current.plusDays(1);
        }
        return ResponseEntity.ok("Sincronización de juegos completada desde " + start + " hasta " + end);
    }
}