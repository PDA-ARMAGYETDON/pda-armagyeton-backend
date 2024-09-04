package com.example.stock_system.trade.dto;

import com.example.stock_system.enums.TradeType;
import lombok.Getter;

@Getter
public class CreateTradeRequest {
    private int accountId;
    private TradeType tradeType;
    private String stockCode;
    private int quantity;
    private int price;
}
