package com.tumundomlb.moundmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "batters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batter {

    @Id
    private Integer id;   // ID de MLB

    @Column(nullable = false)
    private String fullName;

    private String firstName;
    private String lastName;

    @Column(name = "bats_hand", length = 1)
    private String batsHand;   // "R", "L", "S"

    @Column(name = "primary_position", length = 2)
    private String primaryPosition;   // "DH", "1B", "OF", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    @Builder.Default
    @OneToMany(mappedBy = "batter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BattingAppearance> appearances = new ArrayList<>();
}