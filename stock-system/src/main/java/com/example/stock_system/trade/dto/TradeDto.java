package com.example.stock_system.trade.dto;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeType;
import com.example.stock_system.stocks.Stocks;
import com.example.stock_system.trade.Trade;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TradeDto {
    private Account account;
    private TradeType type;
    private Stocks stocks;
    private int quantity;
    private int price;
}
