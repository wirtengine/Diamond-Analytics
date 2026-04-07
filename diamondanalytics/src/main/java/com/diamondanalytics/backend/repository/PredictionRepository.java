package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Optional<Prediction> findByGame(Game game);
    boolean existsByGame(Game game);
}