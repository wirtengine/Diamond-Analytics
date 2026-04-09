package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.PitchingAppearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PitchingAppearanceRepository extends JpaRepository<PitchingAppearance, Long> {

    // 🔍 Buscar una aparición específica por pitcher y juego
    Optional<PitchingAppearance> findByPitcherIdAndGameId(Integer pitcherId, Long gameId);

    // 📅 Obtener apariciones recientes de un pitcher
    List<PitchingAppearance> findByPitcherIdAndGameDateBetweenOrderByGameDateDesc(
            Integer pitcherId, LocalDate start, LocalDate end);

    // 🔥 Obtener SOLO relevistas (RP)
    @Query("SELECT pa FROM PitchingAppearance pa " +
            "JOIN pa.game g " +
            "JOIN pa.pitcher p " +
            "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
            "AND p.primaryPosition = :position " +
            "AND pa.gameDate BETWEEN :start AND :end")
    List<PitchingAppearance> findTeamBullpenAppearances(
            @Param("teamId") Integer teamId,
            @Param("position") String position,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // 🚀 NUEVO: TODAS las apariciones del equipo (sin filtro)
    @Query("SELECT pa FROM PitchingAppearance pa " +
            "JOIN pa.game g " +
            "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
            "AND pa.gameDate BETWEEN :start AND :end")
    List<PitchingAppearance> findAllTeamAppearances(
            @Param("teamId") Integer teamId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}