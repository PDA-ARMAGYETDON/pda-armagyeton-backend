package com.example.stock_system.stocks.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockPriceData {
    private String date;
    private int price;
}
