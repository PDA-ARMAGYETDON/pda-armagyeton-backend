package com.example.stock_system.trade.dto;

import com.example.stock_system.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradeResponse {
    private String stockName;
    private TradeType type;
    private int price;
    private int quantity;
    private LocalDateTime tradeDate;

    @Builder
    public TradeResponse(String stockName, TradeType type, int price, int quantity, LocalDateTime tradeDate) {
        this.stockName = stockName;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.tradeDate = tradeDate;
    }
}
