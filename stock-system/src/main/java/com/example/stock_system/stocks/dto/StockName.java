package com.example.stock_system.stocks.dto;

import com.example.stock_system.stocks.Stocks;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StockName {
    private String code;
    private String name;

    public StockName() {
    }

    @Builder
    public StockName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public StockName fromEntity(Stocks stocks) {
        return StockName.builder()
                .code(stocks.getCode())
                .name(stocks.getName())
                .build();
    }
}
