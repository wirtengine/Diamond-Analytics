package com.diamondanalytics.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledDataIngestion {

    private static final Logger log = LoggerFactory.getLogger(ScheduledDataIngestion.class);

    @Autowired
    private DataIngestionService dataIngestionService;

    /**
     * Tarea programada que se ejecuta todos los días a las 4:00 AM.
     * Realiza una ingesta completa de juegos y odds desde The Odds API.
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void ingestGamesAndOddsDaily() {
        log.info("=== EJECUTANDO TAREA PROGRAMADA: INGESTA DIARIA (4:00 AM) ===");
        try {
            dataIngestionService.ingestGamesAndOdds();
            log.info("Ingesta diaria completada con éxito.");
        } catch (Exception e) {
            log.error("Error en la ingesta diaria: {}", e.getMessage(), e);
        }
    }

    /**
     * Tarea programada que se ejecuta cada hora entre las 8:00 AM y las 11:00 PM.
     * Refresca las odds en vivo durante el horario de juegos.
     */
    @Scheduled(cron = "0 0 8-23 * * ?")
    public void refreshOdds() {
        log.info("=== EJECUTANDO TAREA PROGRAMADA: ACTUALIZACIÓN DE ODDS (cada hora) ===");
        try {
            dataIngestionService.ingestGamesAndOdds(); // Vuelve a ejecutar la misma ingesta (actualiza odds y juegos)
            log.info("Actualización de odds completada.");
        } catch (Exception e) {
            log.error("Error en la actualización de odds: {}", e.getMessage(), e);
        }
    }
}