package com.tumundomlb.moundmetrics.service;

import com.tumundomlb.moundmetrics.dto.WorkloadDTO;
import com.tumundomlb.moundmetrics.entity.PitchingAppearance;
import com.tumundomlb.moundmetrics.repository.PitchingAppearanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PitcherWorkloadService {

    private final PitchingAppearanceRepository appearanceRepository;

    public WorkloadDTO getRecentWorkload(Integer pitcherId, int daysBack) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(daysBack);

        List<PitchingAppearance> apps = appearanceRepository
                .findByPitcherIdAndGameDateBetweenOrderByGameDateDesc(pitcherId, start, end);

        // 🔥 FILTRAR solo apariciones reales
        List<PitchingAppearance> appearancesValidas = apps.stream()
                .filter(app -> app.getPitchesThrown() != null && app.getPitchesThrown() > 0)
                .toList();

        int totalPitches = appearancesValidas.stream()
                .mapToInt(PitchingAppearance::getPitchesThrown)
                .sum();

        int totalGames = appearancesValidas.size();

        PitchingAppearance lastApp = appearancesValidas.isEmpty() ? null : appearancesValidas.get(0);
        Integer pitchesLastGame = lastApp != null ? lastApp.getPitchesThrown() : 0;
        LocalDate lastGameDate = lastApp != null ? lastApp.getGameDate() : null;

        return WorkloadDTO.builder()
                .pitcherId(pitcherId)
                .daysBack(daysBack)
                .gamesPlayed(totalGames)
                .totalPitches(totalPitches)
                .pitchesLastGame(pitchesLastGame)
                .lastGameDate(lastGameDate)
                .build();
    }
}