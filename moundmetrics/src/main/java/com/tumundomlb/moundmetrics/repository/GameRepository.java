package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // 🔥 Obtiene el último juego registrado por fecha oficial
    Optional<Game> findTopByOrderByOfficialDateDesc();
}