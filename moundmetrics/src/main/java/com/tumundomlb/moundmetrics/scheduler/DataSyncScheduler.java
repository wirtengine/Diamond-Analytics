package com.tumundomlb.moundmetrics.scheduler;

import com.tumundomlb.moundmetrics.service.GameSyncService;
import com.tumundomlb.moundmetrics.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSyncScheduler {

    private final TeamService teamService;
    private final GameSyncService gameSyncService;

    @Scheduled(fixedDelay = 604800000) // 7 días
    public void syncTeams() {
        log.info("Scheduled team sync started");
        teamService.syncTeams();
        log.info("Scheduled team sync completed");
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void syncYesterdayGames() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Scheduled game sync for {}", yesterday);
        gameSyncService.syncGamesForDate(yesterday);
    }
}