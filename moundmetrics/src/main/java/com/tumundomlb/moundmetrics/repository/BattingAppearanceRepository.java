package com.tumundomlb.moundmetrics.repository;

import com.tumundomlb.moundmetrics.entity.BattingAppearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BattingAppearanceRepository extends JpaRepository<BattingAppearance, Long> {

    Optional<BattingAppearance> findByBatterIdAndGameId(Integer batterId, Long gameId);

    List<BattingAppearance> findByBatterIdAndGameDateBetweenOrderByGameDateDesc(
            Integer batterId, LocalDate start, LocalDate end);

    @Query("SELECT ba FROM BattingAppearance ba " +
            "JOIN ba.game g " +
            "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
            "AND ba.gameDate BETWEEN :start AND :end")
    List<BattingAppearance> findAllTeamBattingAppearances(
            @Param("teamId") Integer teamId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}