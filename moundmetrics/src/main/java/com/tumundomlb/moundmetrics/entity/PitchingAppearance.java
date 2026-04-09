package com.tumundomlb.moundmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pitching_appearances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pitcher_id", "game_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PitchingAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pitcher_id", nullable = false)
    @JsonIgnore   // Opcional: evita serializar el pitcher completo
    private Pitcher pitcher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore   // Opcional
    private Game game;

    private boolean starter;

    @Column(name = "innings_pitched", precision = 4, scale = 2)
    private BigDecimal inningsPitched;

    private Integer hits;
    private Integer runs;

    @Column(name = "earned_runs")
    private Integer earnedRuns;

    @Column(name = "base_on_balls")
    private Integer baseOnBalls;

    @Column(name = "strike_outs")
    private Integer strikeOuts;

    @Column(name = "home_runs")
    private Integer homeRuns;

    @Column(name = "pitches_thrown")
    private Integer pitchesThrown;

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