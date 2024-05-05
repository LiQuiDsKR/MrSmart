package com.care4u.toolbox.stock_status;

import java.time.LocalDate;
import java.util.List;

import com.care4u.toolbox.group.main_group.MainGroupDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockStatusSummaryByToolboxDto {
    private String toolboxName;
    private long count;
}