package com.care4u.toolbox.stock_status;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockStatusSummaryByToolStateDto {
    private String toolboxName;
    private LocalDate currentDay;
    private Integer totalCount;
    private Integer rentalCount;
    private Integer buyCount;
    private Integer goodCount;
    private Integer faultCount;
    private Integer damageCount;
    private Integer lossCount;
    private Integer discardCount;
}
