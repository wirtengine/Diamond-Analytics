package com.tumundomlb.moundmetrics.scheduler;

import com.tumundomlb.moundmetrics.entity.Game;
import com.tumundomlb.moundmetrics.repository.GameRepository;
import com.tumundomlb.moundmetrics.service.GameSyncService;
import com.tumundomlb.moundmetrics.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupDataSync {

    private final TeamService teamService;
    private final GameSyncService gameSyncService;
    private final GameRepository gameRepository;

    private static final LocalDate SEASON_START_2026 = LocalDate.of(2026, 3, 26);

    @EventListener(ApplicationReadyEvent.class)
    public void syncHistoricalData() {
        log.info("=== INICIANDO SINCRONIZACIÓN AUTOMÁTICA DE DATOS HISTÓRICOS ===");

        // 1. Sincronizar equipos
        log.info("Sincronizando equipos MLB...");
        teamService.syncTeams();

        // 2. Determinar fecha de inicio para la sincronización de juegos
        LocalDate startDate = SEASON_START_2026;
        Optional<Game> latestGame = gameRepository.findTopByOrderByOfficialDateDesc();
        if (latestGame.isPresent()) {
            LocalDate lastDate = latestGame.get().getOfficialDate();
            startDate = lastDate.plusDays(1);
            log.info("Último juego en BD: {}. Sincronizando desde {}", lastDate, startDate);
        } else {
            log.info("No hay juegos en BD. Sincronizando desde inicio de temporada: {}", SEASON_START_2026);
        }

        LocalDate endDate = LocalDate.now().minusDays(1); // hasta ayer

        if (startDate.isAfter(endDate)) {
            log.info("La base de datos ya está actualizada hasta ayer. No se requiere sincronización histórica.");
            return;
        }

        log.info("Sincronizando juegos desde {} hasta {}", startDate, endDate);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            try {
                gameSyncService.syncGamesForDate(current);
                log.info("✔ Fecha {} procesada", current);
            } catch (Exception e) {
                log.error("✖ Error procesando fecha {}: {}", current, e.getMessage());
            }
            current = current.plusDays(1);
        }

        log.info("=== SINCRONIZACIÓN AUTOMÁTICA COMPLETADA ===");
    }
}