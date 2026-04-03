package com.diamondanalytics.backend.controller;

import com.diamondanalytics.backend.dto.DashboardSummaryResponse;
import com.diamondanalytics.backend.model.User;
import com.diamondanalytics.backend.repository.UserRepository;
import com.diamondanalytics.backend.security.UserDetailsImpl;
import com.diamondanalytics.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDashboardSummary() {
        // Obtener el usuario autenticado desde el contexto de seguridad
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DashboardSummaryResponse summary = dashboardService.getSummaryForUser(user);
        return ResponseEntity.ok(summary);
    }
}