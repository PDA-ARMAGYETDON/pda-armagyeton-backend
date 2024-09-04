package com.example.group_investment.tradeOffer.dto;

import com.example.group_investment.enums.TradeType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateTradeRequest {
    private int accountId;
    private TradeType tradeType;
    private String stockCode;
    private int quantity;
    private int price;

    public CreateTradeRequest() {
    }

    @Builder
    public CreateTradeRequest(int accountId, TradeType tradeType, String stockCode, int quantity, int price) {
        this.accountId = accountId;
        this.tradeType = tradeType;
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.price = price;
    }
}
