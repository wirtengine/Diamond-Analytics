package com.tumundomlb.moundmetrics.controller;

import com.tumundomlb.moundmetrics.dto.BullpenStatsDTO;
import com.tumundomlb.moundmetrics.entity.Team;
import com.tumundomlb.moundmetrics.repository.TeamRepository;
import com.tumundomlb.moundmetrics.service.BullpenStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final BullpenStatsService bullpenStatsService;

    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/{teamId}/bullpen-stats")
    public BullpenStatsDTO getBullpenStats(
            @PathVariable Integer teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now();

        return bullpenStatsService.getTeamBullpenStats(teamId, from, to);
    }
}