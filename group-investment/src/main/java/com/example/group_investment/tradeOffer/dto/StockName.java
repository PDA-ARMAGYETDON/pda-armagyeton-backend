package com.example.group_investment.tradeOffer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StockName {
    private String code;
    private String name;

    @Builder
    public StockName(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
