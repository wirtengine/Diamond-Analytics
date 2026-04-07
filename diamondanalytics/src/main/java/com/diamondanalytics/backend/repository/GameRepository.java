package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByExternalId(String externalId);
    List<Game> findByStartTimeBetween(Instant start, Instant end); // nuevo
}