package com.tumundomlb.moundmetrics.controller;

import com.tumundomlb.moundmetrics.dto.WorkloadDTO;
import com.tumundomlb.moundmetrics.dto.PitchingAppearanceDto;
import com.tumundomlb.moundmetrics.dto.PitcherStatsDTO;
import com.tumundomlb.moundmetrics.entity.Pitcher;
import com.tumundomlb.moundmetrics.entity.PitchingAppearance;
import com.tumundomlb.moundmetrics.service.PitcherWorkloadService;
import com.tumundomlb.moundmetrics.service.PitcherStatsService;
import com.tumundomlb.moundmetrics.repository.PitcherRepository;
import com.tumundomlb.moundmetrics.repository.PitchingAppearanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pitchers")
@RequiredArgsConstructor
public class PitcherController {

    private final PitcherRepository pitcherRepository;
    private final PitchingAppearanceRepository appearanceRepository;
    private final PitcherWorkloadService workloadService;
    private final PitcherStatsService pitcherStatsService;

    // 🔥 Workload reciente del pitcher
    @GetMapping("/{pitcherId}/workload")
    public WorkloadDTO getWorkload(
            @PathVariable Integer pitcherId,
            @RequestParam(defaultValue = "7") int days) {

        return workloadService.getRecentWorkload(pitcherId, days);
    }

    // 🔥 Pitchers por equipo
    @GetMapping("/team/{teamId}")
    public List<Pitcher> getPitchersByTeam(@PathVariable Integer teamId) {
        return pitcherRepository.findByTeamId(teamId);
    }

    // 🔥 Apariciones recientes
    @GetMapping("/{pitcherId}/appearances")
    public List<PitchingAppearanceDto> getRecentAppearances(
            @PathVariable Integer pitcherId,
            @RequestParam(defaultValue = "7") int days) {

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);

        return appearanceRepository
                .findByPitcherIdAndGameDateBetweenOrderByGameDateDesc(pitcherId, start, end)
                .stream()
                .map(this::mapToDto)
                .toList(); // Java 17+
    }

    // 🔥 Estadísticas del pitcher
    @GetMapping("/{pitcherId}/stats")
    public PitcherStatsDTO getPitcherStats(
            @PathVariable Integer pitcherId,
            @RequestParam(defaultValue = "7") int days) {

        return pitcherStatsService.getPitcherStats(pitcherId, days);
    }

    // ✅ Mapper reutilizable (mejor práctica)
    private PitchingAppearanceDto mapToDto(PitchingAppearance app) {
        return new PitchingAppearanceDto(
                app.getGameDate(),
                app.getPitchesThrown(),
                app.getInningsPitched()
        );
    }
}