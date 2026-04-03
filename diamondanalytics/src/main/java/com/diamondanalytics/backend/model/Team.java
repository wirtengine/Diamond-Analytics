package com.diamondanalytics.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String externalId; // ID de la API externa (ej: "cle:team:123" o "9")

    @Column(nullable = false)
    private String name;

    private String abbreviation;

    private String league; // "MLB"

    private String city;   // opcional

    private String logoUrl; // opcional
}