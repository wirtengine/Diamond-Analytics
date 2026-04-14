package com.tumundomlb.moundmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fielders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fielder {

    @Id
    private Integer id;   // ID de MLB

    @Column(nullable = false)
    private String fullName;

    private String firstName;
    private String lastName;

    @Column(name = "primary_position", length = 3)
    private String primaryPosition;   // "SS", "CF", "1B", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    @Builder.Default
    @OneToMany(mappedBy = "fielder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FieldingAppearance> appearances = new ArrayList<>();
}