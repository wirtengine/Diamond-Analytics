package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByExternalId(String externalId);

    List<Game> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT g FROM Game g WHERE g.startTime BETWEEN :start AND :end ORDER BY g.startTime ASC")
    List<Game> findGamesInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}