package com.tumundomlb.moundmetrics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, length = 3)
    private String abbreviation;

    @Column(length = 20)
    private String league;

    @Column(length = 50)
    private String division;

    @Builder.Default
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    @JsonIgnore   // Evita serializar la lista de pitchers al enviar Team
    private List<Pitcher> pitchers = new ArrayList<>();
}