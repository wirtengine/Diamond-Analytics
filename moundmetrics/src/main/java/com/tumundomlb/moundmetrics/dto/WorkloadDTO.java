package com.tumundomlb.moundmetrics.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WorkloadDTO {
    private Integer pitcherId;
    private int daysBack;
    private int gamesPlayed;
    private int totalPitches;
    private Integer pitchesLastGame;
    private LocalDate lastGameDate;
}