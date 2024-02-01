package com.care4u.toolbox.stock_status;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Integer supplyCount;
    private Integer returnCount;
    
    public StockStatusSummaryByToolStateDto(StockStatusSummaryByToolStateDto source) {
        this.toolboxName = source.toolboxName;
        this.currentDay = source.currentDay;
        this.totalCount = source.totalCount;
        this.rentalCount = source.rentalCount;
        this.buyCount = source.buyCount;
        this.goodCount = source.goodCount;
        this.faultCount = source.faultCount;
        this.damageCount = source.damageCount;
        this.lossCount = source.lossCount;
        this.discardCount = source.discardCount;
        this.supplyCount = source.supplyCount;
        this.returnCount = source.returnCount;
    }

    
    public StockStatusSummaryByToolStateDto update(Integer rentalCount, Integer returnCount) {
    	this.rentalCount=rentalCount;
    	this.returnCount=returnCount;
    	return this;
    }
}
