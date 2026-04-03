package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Odds;
import com.diamondanalytics.backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OddsRepository extends JpaRepository<Odds, Long> {
    Optional<Odds> findByGameAndBookmakerAndTimestamp(Game game, String bookmaker, LocalDateTime timestamp);
    List<Odds> findByGame(Game game);
    List<Odds> findByTimestampAfter(LocalDateTime timestamp);
}