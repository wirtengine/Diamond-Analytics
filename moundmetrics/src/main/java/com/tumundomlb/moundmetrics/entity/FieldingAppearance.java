package com.tumundomlb.moundmetrics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "fielding_appearances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fielder_id", "game_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldingAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fielder_id", nullable = false)
    private Fielder fielder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    // Estadísticas defensivas del juego
    private Integer putOuts;          // PO
    private Integer assists;          // A
    private Integer errors;           // E
    private Integer doublePlays;      // DP
    private Integer triplePlays;      // TP
    private Integer gamesPlayed;      // G (generalmente 1 por juego)
    private Integer gamesStarted;     // GS

    // Posición jugada en este juego
    private String position;          // Código: "SS", "CF", etc.

    @Column(name = "game_date")
    private LocalDate gameDate;

    @PrePersist
    @PreUpdate
    private void syncGameDate() {
        if (game != null) {
            this.gameDate = game.getOfficialDate();
        }
    }
}