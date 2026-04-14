package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.FieldingAppearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FieldingAppearanceRepository extends JpaRepository<FieldingAppearance, Long> {

    Optional<FieldingAppearance> findByFielderIdAndGameId(Integer fielderId, Long gameId);

    List<FieldingAppearance> findByFielderIdAndGameDateBetweenOrderByGameDateDesc(
            Integer fielderId, LocalDate start, LocalDate end);

    @Query("SELECT fa FROM FieldingAppearance fa " +
            "JOIN fa.game g " +
            "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
            "AND fa.gameDate BETWEEN :start AND :end")
    List<FieldingAppearance> findAllTeamFieldingAppearances(
            @Param("teamId") Integer teamId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}