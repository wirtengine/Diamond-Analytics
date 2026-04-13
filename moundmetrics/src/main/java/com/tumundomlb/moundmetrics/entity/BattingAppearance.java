package com.tumundomlb.moundmetrics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "batting_appearances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"batter_id", "game_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattingAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batter_id", nullable = false)
    private Batter batter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    // Estadísticas ofensivas del juego
    private Integer atBats;          // AB
    private Integer runs;            // R
    private Integer hits;            // H
    private Integer doubles;         // 2B
    private Integer triples;         // 3B
    private Integer homeRuns;        // HR
    private Integer rbi;             // RBI
    private Integer baseOnBalls;     // BB
    private Integer intentionalWalks;// IBB
    private Integer strikeOuts;      // SO
    private Integer stolenBases;     // SB
    private Integer caughtStealing;  // CS
    private Integer hitByPitch;      // HBP
    private Integer sacFlies;        // SF
    private Integer sacBunts;        // SH
    private BigDecimal avg;          // AVG (calculado o extraído)
    private BigDecimal obp;          // OBP
    private BigDecimal slg;          // SLG
    private BigDecimal ops;          // OPS

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