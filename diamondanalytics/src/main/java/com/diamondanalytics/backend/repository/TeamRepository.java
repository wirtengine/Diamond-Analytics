package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByExternalId(String externalId);
    Optional<Team> findByName(String name);   // ← Nuevo método
}