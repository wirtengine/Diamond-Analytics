package com.diamondanalytics.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;  // opcional, para asociar a un juego específico

    @Column(nullable = false)
    private Double amount;  // monto apostado

    @Column(nullable = false)
    private Double odds;    // cuota a la que se apostó

    private String betType; // ej: "moneyline", "spread", "over/under"

    private String selection; // equipo o resultado elegido

    @Enumerated(EnumType.STRING)
    private BetResult result; // GANADA, PERDIDA, PENDIENTE

    private Double profit;    // beneficio (si result=GANADA: amount*(odds-1), si PERDIDA: -amount)

    private LocalDateTime betDate;

    @PrePersist
    protected void onCreate() {
        betDate = LocalDateTime.now();
        if (profit == null && result != null) {
            calculateProfit();
        }
    }

    public void calculateProfit() {
        if (result == BetResult.GANADA) {
            this.profit = amount * (odds - 1);
        } else if (result == BetResult.PERDIDA) {
            this.profit = -amount;
        } else {
            this.profit = 0.0;
        }
    }
}