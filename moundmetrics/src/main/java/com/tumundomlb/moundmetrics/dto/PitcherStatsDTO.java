package com.tumundomlb.moundmetrics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PitcherStatsDTO {
    private Integer pitcherId;
    private String fullName;
    private int gamesPlayed;
    private BigDecimal era;
    private BigDecimal whip;
    private BigDecimal inningsPitched;
    private int strikeOuts;
    private int baseOnBalls;
    private int hits;
    private int earnedRuns;
    private BigDecimal kPer9;
    private BigDecimal bbPer9;
}