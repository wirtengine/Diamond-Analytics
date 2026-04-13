package com.tumundomlb.moundmetrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BattingAppearanceDto {
    private LocalDate gameDate;
    private Integer hits;
    private Integer atBats;
    private Integer homeRuns;
}