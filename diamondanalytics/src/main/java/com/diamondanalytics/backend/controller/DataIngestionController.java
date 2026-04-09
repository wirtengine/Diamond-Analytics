package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.service.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class DataIngestionController {

    @Autowired
    private DataIngestionService dataIngestionService;

    @PostMapping("/ingest")
    @PreAuthorize("isAuthenticated()")  // Cambiado: solo requiere login, no rol ADMIN
    public String ingest() {
        dataIngestionService.ingestGamesAndOdds();  // ← Método correcto
        return "Ingestión de juegos y odds iniciada. Revisa los logs.";
    }
}