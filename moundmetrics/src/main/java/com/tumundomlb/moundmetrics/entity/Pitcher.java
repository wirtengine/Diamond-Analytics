package com.tumundomlb.moundmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pitchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pitcher {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "throws_hand", length = 1)
    private String throwsHand;

    @Column(name = "primary_position", length = 2)
    private String primaryPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore   // Evita serializar el equipo al enviar Pitcher (rompe ciclo)
    private Team team;

    @Builder.Default
    @OneToMany(mappedBy = "pitcher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore   // También ignoramos las apariciones en la serialización básica
    private List<PitchingAppearance> appearances = new ArrayList<>();
}