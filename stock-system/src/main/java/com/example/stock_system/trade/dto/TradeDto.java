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

    public TradeDto() {
    }

    @Builder
    public TradeDto(Account account, TradeType type, Stocks stocks, int quantity, int price) {
        this.account = account;
        this.type = type;
        this.stocks = stocks;
        this.quantity = quantity;
        this.price = price;
    }

    public Trade toEntity() {
        return Trade.builder()
                .account(this.account)
                .type(this.type)
                .stockCode(this.stocks)
                .quantity(this.quantity)
                .price(this.price)
                .build();
    }
}
