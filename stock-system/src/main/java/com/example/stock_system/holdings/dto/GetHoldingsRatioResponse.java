package com.example.stock_system.holdings.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetHoldingsRatioResponse {
    private String stockName;
    private double ratio;

    @Builder
    public GetHoldingsRatioResponse(String stockName, double ratio) {
        this.stockName = stockName;
        this.ratio = ratio;
    }
}
