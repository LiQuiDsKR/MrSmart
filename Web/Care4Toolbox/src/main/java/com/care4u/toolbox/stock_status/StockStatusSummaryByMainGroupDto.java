package com.care4u.toolbox.stock_status;

import java.time.LocalDate;

import com.care4u.toolbox.group.main_group.MainGroupDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockStatusSummaryByMainGroupDto {
    private String toolboxName;
    private LocalDate currentDay;
    private MainGroupDto mainGroupDto;
    private Integer totalCount;
}
