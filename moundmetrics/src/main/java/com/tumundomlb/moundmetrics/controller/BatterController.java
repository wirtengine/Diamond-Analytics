package com.tumundomlb.moundmetrics.controller;

import com.tumundomlb.moundmetrics.dto.BatterStatsDTO;
import com.tumundomlb.moundmetrics.dto.BattingAppearanceDto;
import com.tumundomlb.moundmetrics.entity.Batter;
import com.tumundomlb.moundmetrics.entity.BattingAppearance;
import com.tumundomlb.moundmetrics.repository.BatterRepository;
import com.tumundomlb.moundmetrics.repository.BattingAppearanceRepository;
import com.tumundomlb.moundmetrics.service.BatterStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batters")
@RequiredArgsConstructor
public class BatterController {

    private final BatterRepository batterRepository;
    private final BatterStatsService batterStatsService;
    private final BattingAppearanceRepository battingAppearanceRepository; // 👈 Añadir este repositorio

    @GetMapping("/team/{teamId}")
    public List<Batter> getBattersByTeam(@PathVariable Integer teamId) {
        return batterRepository.findByTeamId(teamId);
    }

    @GetMapping("/{batterId}/stats")
    public BatterStatsDTO getBatterStats(
            @PathVariable Integer batterId,
            @RequestParam(defaultValue = "7") int days) {
        return batterStatsService.getBatterStats(batterId, days);
    }

    // ✅ NUEVO ENDPOINT PARA APARICIONES
    @GetMapping("/{batterId}/appearances")
    public List<BattingAppearanceDto> getRecentAppearances(
            @PathVariable Integer batterId,
            @RequestParam(defaultValue = "7") int days) {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);

        List<BattingAppearance> appearances = battingAppearanceRepository
                .findByBatterIdAndGameDateBetweenOrderByGameDateDesc(batterId, start, end);

        return appearances.stream()
                .map(app -> new BattingAppearanceDto(
                        app.getGameDate(),
                        app.getHits() != null ? app.getHits() : 0,
                        app.getAtBats() != null ? app.getAtBats() : 0,
                        app.getHomeRuns() != null ? app.getHomeRuns() : 0))
                .collect(Collectors.toList());
    }
}