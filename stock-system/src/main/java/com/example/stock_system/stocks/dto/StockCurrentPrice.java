package com.example.stock_system.stocks.dto;

import lombok.Getter;

@Getter
public class StockCurrentPrice {
    private final int currentPrice;
    private final double changeRate;

    public StockCurrentPrice(int currentPrice, double changeRate) {
        this.currentPrice = currentPrice;
        this.changeRate = changeRate;
    }


}
