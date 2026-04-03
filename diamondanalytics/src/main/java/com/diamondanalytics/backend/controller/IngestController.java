package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.service.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {

    @Autowired
    private DataIngestionService dataIngestionService;

    @PostMapping("/games")
    @PreAuthorize("isAuthenticated()") // Cambia a isAuthenticated() para probar
    public String ingestGames() {
        dataIngestionService.ingestGamesAndOdds();
        return "Ingesta iniciada. Revisa los logs.";
    }
}