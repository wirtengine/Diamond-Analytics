package com.diamondanalytics.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "games", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"externalId"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String externalId; // ID de API (ej: "game:12345")

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    private LocalDateTime startTime;

    private String status; // "scheduled", "live", "finished"

    private Integer homeScore;
    private Integer awayScore;

    // Para estadísticas adicionales (opcional)
    private Integer homeHits;
    private Integer awayHits;
    private Integer homeErrors;
    private Integer awayErrors;
}