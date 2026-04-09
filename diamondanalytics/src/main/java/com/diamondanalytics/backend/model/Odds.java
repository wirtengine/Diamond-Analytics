package com.diamondanalytics.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "odds", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "bookmaker", "timestamp"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Odds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private String bookmaker; // ej: "draftkings", "fanduel", "bet365"

    private Double homeMoneyline;
    private Double awayMoneyline;
    private Double spread;     // hándicap
    private Double overUnder;  // total de carreras

    private LocalDateTime timestamp; // cuando se obtuvieron estas odds
}