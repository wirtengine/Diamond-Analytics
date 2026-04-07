package com.diamondanalytics.backend.repository;

import com.diamondanalytics.backend.model.Game;
import com.diamondanalytics.backend.model.Odds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OddsRepository extends JpaRepository<Odds, Long> {

    // Método existente (si ya lo tienes)
    List<Odds> findByGame(Game game);

    // NUEVO MÉTODO: Obtener odds ordenadas por timestamp descendente
    List<Odds> findByGameOrderByTimestampDesc(Game game);
}