package com.example.stock_system.trade.dto;

import com.example.stock_system.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetTradeResponse {
    private String stockName;
    private TradeType type;
    private int price;
    private int quantity;
    private String tradeDate;

    @Builder
    public GetTradeResponse(String stockName, TradeType type, int price, int quantity, String tradeDate) {
        this.stockName = stockName;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.tradeDate = tradeDate;
    }
}
