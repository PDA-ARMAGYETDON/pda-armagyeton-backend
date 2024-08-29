package com.example.stock_system.pre_trade.dto;

import com.example.stock_system.account.Account;
import com.example.stock_system.enums.TradeStatus;
import com.example.stock_system.pre_trade.PreTrade;
import com.example.stock_system.stocks.Stocks;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PreTradeDto {
    private int accountId;
    private LocalDate startAt;
    private LocalDate endAt;
    private TradeStatus status;
    private int price;
    private int quantity;
    private LocalDateTime createdAt;
    private String stockCode;

    public PreTrade toEntity(Account account, Stocks stocks) {
        return PreTrade.builder()
                .account(account)
                .startAt(this.startAt)
                .endAt(this.endAt)
                .status(this.status)
                .price(this.price)
                .quantity(this.quantity)
                .createdAt(this.createdAt)
                .stocks(stocks)
                .build();
    }
}
