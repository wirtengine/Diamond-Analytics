package com.diamondanalytics.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private Double homeWinProbability;   // 0.0 - 1.0

    @Column(nullable = false)
    private Double awayWinProbability;

    private String recommendedBet;       // "HOME", "AWAY", "OVER", "UNDER", etc.
    private Double confidenceScore;      // 0.0 - 1.0

    @Column(columnDefinition = "TEXT")
    private String analysis;             // Explicación generada por IA

    private Double expectedValue;        // EV calculado

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}