package com.diamondanalytics.backend.dto.prediction;

import lombok.Data;

@Data
public class GamePredictionDto {
    private Long gameId;
    private String homeTeam;
    private String awayTeam;
    private String startTime;
    private Double homeWinProbability;
    private Double awayWinProbability;
    private String recommendedBet;
    private Double confidenceScore;
    private String analysis;
    private Double expectedValue;
}