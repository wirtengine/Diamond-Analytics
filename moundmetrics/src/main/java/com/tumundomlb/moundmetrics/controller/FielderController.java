package com.tumundomlb.moundmetrics.controller;

import com.tumundomlb.moundmetrics.dto.FieldingStatsDTO;
import com.tumundomlb.moundmetrics.entity.Fielder;
import com.tumundomlb.moundmetrics.repository.FielderRepository;
import com.tumundomlb.moundmetrics.service.FieldingStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fielders")
@RequiredArgsConstructor
public class FielderController {

    private final FielderRepository fielderRepository;
    private final FieldingStatsService fieldingStatsService;

    @GetMapping("/team/{teamId}")
    public List<Fielder> getFieldersByTeam(@PathVariable Integer teamId) {
        return fielderRepository.findByTeamId(teamId);
    }

    @GetMapping("/{fielderId}/stats")
    public FieldingStatsDTO getFieldingStats(
            @PathVariable Integer fielderId,
            @RequestParam(defaultValue = "7") int days) {
        return fieldingStatsService.getFieldingStats(fielderId, days);
    }
}