package com.tumundomlb.moundmetrics.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BullpenStatsDTO {
    private Integer teamId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalInnings;
    private Integer earnedRuns;
    private BigDecimal era;
    private BigDecimal whip;
    private Integer appearancesCount;
}